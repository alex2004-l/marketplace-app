package com.example.ioservice.service;

import com.example.ioservice.dto.*;
import com.example.ioservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.ioservice.dto.ReviewDTOs.ReviewDTO;
import com.example.ioservice.dto.UserDTOs.AddUserDto;
import com.example.ioservice.dto.UserDTOs.UserDTO;
import com.example.ioservice.dto.UserDTOs.UserUpdateDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemDTO;
import com.example.ioservice.model.*;
import com.example.ioservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IOService {
    private final ProductRepository      productRepository;
    private final UserRepository         userRepository;
    private final CartRepository         cartRepository;
    private final CartProductRepository  cartProductRepository;
    private final OrderRepository        orderRepository;
    private final WishlistRepository     wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ReviewRepository       reviewRepository;

    public ProductDto addProduct(ProductSecDto productDto) {
        UserModel userModel = userRepository.findByKeycloakId(productDto.keycloakId())
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        ProductModel productModel = ProductModel.builder()
                .productId(productDto.productId())
                .productName(productDto.productName())
                .price(productDto.price())
                .description(productDto.description())
                .sellerId(userModel.getUserId())
                .quantity(productDto.quantity())
                .build();
        productRepository.save(productModel);

        return new ProductDto(productModel.getProductId(), productModel.getProductName(), productModel.getPrice(),
                productModel.getDescription(), productModel.getSellerId(), productModel.getQuantity());
    }

    public List<ProductDto> seeAllProducts() {
        return productRepository.findAll().stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getSellerId(),
                        productModel.getQuantity())).toList();
    }

    public ProductDto getProductById(Long productId) {
        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        return productModelOptional.map(productModel -> new ProductDto(productModel.getProductId(),
                productModel.getProductName(), productModel.getPrice(), productModel.getDescription(),
                productModel.getSellerId(), productModel.getQuantity())).orElseThrow(() -> new RuntimeException("No product with id " + productId + "!"));
    }

    @Transactional
    public ProductDto updateProduct(Long productId, ProductUpdateDTO productUpdateDTO, String keycloakId) {
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No product with id " + productId));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(()-> new RuntimeException("No user with this keycloak id"));

        if (!productModel.getSellerId().equals(userModel.getUserId())) {
            throw new RuntimeException("Seller not allowed to update this product!");
        }

        if (productUpdateDTO.productName() != null)
            productModel.setProductName(productUpdateDTO.productName());
        if (productUpdateDTO.price() != null)
            productModel.setPrice(productUpdateDTO.price());
        if (productUpdateDTO.description() != null)
            productModel.setDescription(productUpdateDTO.description());
        if (productUpdateDTO.quantity() != null)
            productModel.setQuantity(productUpdateDTO.quantity());

        productRepository.save(productModel);

        return new ProductDto(
                productModel.getProductId(),
                productModel.getProductName(),
                productModel.getPrice(),
                productModel.getDescription(),
                productModel.getSellerId(),
                productModel.getQuantity());
    }

    public List<ProductDto> searchByName(SearchProductDTO searchProductDTO) {
        String sortField = (searchProductDTO.sortBy() == null || searchProductDTO.sortBy().isBlank()) ? "productId" : searchProductDTO.sortBy();
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(searchProductDTO.dir()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, sortField);

        return productRepository.findByProductNameIgnoreCaseStartingWith(searchProductDTO.productName().toLowerCase(), sort).stream()
                .map(productModel -> new ProductDto(
                        productModel.getProductId(),
                        productModel.getProductName(),
                        productModel.getPrice(),
                        productModel.getDescription(),
                        productModel.getSellerId(),
                        productModel.getQuantity())
                ).toList();
    }

    public boolean checkQuantity(Long productId, Long quantity) {
        Optional<ProductModel> productModel = productRepository.findById(productId);

        if (productModel.isPresent()) {
            ProductModel product = productModel.get();
            return product.getQuantity() - quantity >= 0;
        } else {
            return false;
        }
    }

    public String addProductToCart(AddProductToCartDto addProductToCartDto) {
        UserModel userModel = userRepository.findByKeycloakId(addProductToCartDto.keycloakId())
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        CartModel cart = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress")
                .orElseGet(() -> {
                   CartModel newCart = new CartModel();
                   newCart.setUserId(userModel.getUserId());
                   newCart.setStatus("inProgress");
                   return cartRepository.save(newCart);
                });

        Optional<CartProductModel> cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getCartId(), addProductToCartDto.productId());
        if (cartProduct.isPresent()) {
            CartProductModel cartProductModel = cartProduct.get();
            if(checkQuantity(cartProductModel.getProductId(), cartProductModel.getQuantity()
                    + addProductToCartDto.quantity())) {
                cartProductModel.setQuantity(cartProductModel.getQuantity() + addProductToCartDto.quantity());
                cartProductRepository.save(cartProductModel);
            } else {
                cartProductRepository.save(cartProductModel);
                return "Not enough products!";
            }
        } else {
            CartProductModel newCartProduct = new CartProductModel();
            newCartProduct.setCartId(cart.getCartId());
            newCartProduct.setProductId(addProductToCartDto.productId());
            if(checkQuantity(newCartProduct.getProductId(), addProductToCartDto.quantity())) {
                newCartProduct.setQuantity(addProductToCartDto.quantity());
                cartProductRepository.save(newCartProduct);
            } else {
                return "Not enough products!";
            }
            cartProductRepository.save(newCartProduct);
        }

        return "Successfully added product to cart!";
    }

    public String removeOneProduct(RemoveProductFromCartDto dto) {
        UserModel userModel = userRepository.findByKeycloakId(dto.keycloakId())
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));
        CartProductModel cartProductModel = cartProductRepository.findByCartIdAndProductId(cartModel.getCartId(),
                dto.productId()).orElseThrow(() -> new RuntimeException("Product doesn't exist!"));
        cartProductModel.setQuantity(cartProductModel.getQuantity() - 1);
        if (cartProductModel.getQuantity() == 0) {
            cartProductRepository.deleteByCartIdAndProductId(cartModel.getCartId(), dto.productId());
        } else {
            cartProductRepository.save(cartProductModel);
        }

        return "Quantity changed successfully!";
    }

    public String removeProduct(RemoveProductFromCartDto dto) {
        UserModel userModel = userRepository.findByKeycloakId(dto.keycloakId())
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));
        cartProductRepository.deleteByCartIdAndProductId(cartModel.getCartId(), dto.productId());

        return "Product removed from cart!";
    }

    public Float getCartTotal(String userId) {
        UserModel userModel = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));
        List<CartProductModel> products = cartProductRepository.findAllByCartId(cartModel.getCartId());

        float total = 0.0f;
        for (CartProductModel p : products) {
            ProductModel productModel = productRepository.findById(p.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product doesn't exist!"));
            total += productModel.getPrice() * p.getQuantity();
        }

        return total;
    }

    @Transactional
    public void removeCart(Long cartId) {
        Optional<CartModel> cartModel = cartRepository.findById(cartId);

        if (cartModel.isPresent()) {
            List<CartProductModel> products = cartProductRepository.findAllByCartId(cartId);
            cartProductRepository.deleteAll(products);
            cartRepository.delete(cartModel.get());
        }
    }

    @Transactional
    public String makeOrder(String userId) {
        UserModel userModel = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));

        Float cartTotal = getCartTotal(userId);
        if (cartTotal <= 0.0) {
            return "No products in cart!";
        }

        OrderModel orderModel = OrderModel.builder()
                .cartId(cartModel.getCartId())
                .price(getCartTotal(userId))
                .status("inProgress")
                .userId(userModel.getUserId())
                .build();
        orderRepository.save(orderModel);
        boolean paymentDone = orderPayment(orderModel);

        if (paymentDone) {
            updateQuantities(cartModel.getCartId());
            cartModel.setStatus("ready");
            cartRepository.save(cartModel);
            return "Order completed!";
        } else {
            return "Order incomplete!";
        }
    }

    @Transactional
    public boolean orderPayment(OrderModel order) {
        Optional<OrderModel> optOrder = orderRepository.findByCartIdAndStatus(order.getCartId(), order.getStatus());

        if (optOrder.isPresent()) {
            OrderModel currentOrder = optOrder.get();
            currentOrder.setStatus("completed");
            orderRepository.save(currentOrder);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateQuantities(Long cartId) {
        List <CartProductModel> products = cartProductRepository.findAllByCartId(cartId);

        for (CartProductModel cartProductModel : products) {
            ProductModel product = productRepository.findById(cartProductModel.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product doesn't exist!"));
            product.setQuantity(product.getQuantity() - Math.toIntExact(cartProductModel.getQuantity()));
            if (product.getQuantity() == 0) {
                productRepository.deleteById(product.getProductId());
            } else {
                productRepository.save(product);
            }
        }
    }

    public CartDto getCartProducts(String userId) {
        UserModel userModel = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        Optional<CartModel> cartModel = cartRepository.findByUserIdAndStatus(userModel.getUserId(), "inProgress");

        if (cartModel.isPresent()) {
            List<OrderedProductDto> orderedProductDtos = new ArrayList<>();
            List<CartProductModel> products = cartProductRepository.findAllByCartId(cartModel.get().getCartId());
            for (CartProductModel p : products) {
                Optional<ProductModel> optProductModel = productRepository.findById(p.getProductId());
                if (optProductModel.isPresent()) {
                    ProductModel productModel = optProductModel.get();
                    OrderedProductDto orderedProductDto = new OrderedProductDto(productModel.getProductId(),
                            productModel.getProductName(), productModel.getPrice(), p.getQuantity());
                    orderedProductDtos.add(orderedProductDto);
                }
            }
            return new CartDto(getCartTotal(userId), orderedProductDtos);
        } else {
            return new CartDto(0.0F, List.of());
        }
    }

    public List<OrderHistoryDto> getOrdersHistory(String userId) {
        UserModel userModel = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));
        List<OrderModel> orders = orderRepository.findAllByUserId(userModel.getUserId());
        List<OrderHistoryDto> history = new ArrayList<>();
        for (OrderModel order : orders) {
            OrderHistoryDto orderHistoryDto = new OrderHistoryDto(order.getOrderId(), order.getPrice(),
                    order.getStatus(), new ArrayList<>());

            List<CartProductModel> cartProducts = cartProductRepository.findAllByCartId(order.getCartId());
            for (CartProductModel cartProduct : cartProducts) {
                Optional<ProductModel> optProductModel = productRepository.findById(cartProduct.getProductId());
                if (optProductModel.isPresent()) {
                    ProductModel productModel = optProductModel.get();
                    OrderedProductDto orderedProductDto = new OrderedProductDto(productModel.getProductId(),
                            productModel.getProductName(), productModel.getPrice(), cartProduct.getQuantity());
                    orderHistoryDto.products().add(orderedProductDto);
                }
            }
            history.add(orderHistoryDto);
        }
        return history;
    }

    @Transactional
    public void deleteProduct(Long productId, String keycloakId) {
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product doesn't exist!"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!productModel.getSellerId().equals(userModel.getUserId())) {
            throw new RuntimeException("Product doesn't exist!");
        }

        productRepository.deleteById(productId);
    }

    @Transactional
    public WishlistDTO addWishlist(WishlistAddDTO wishlistAddDTO, String keycloakId) {
        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        boolean exists = wishlistRepository.existsByUserIdAndWishlistName(userModel.getUserId(), wishlistAddDTO.wishlistName());
        if (exists) {
            throw new IllegalStateException("Wishlist already exists!");
        }

        WishlistModel wishlist = new WishlistModel();
        wishlist.setWishlistName(wishlistAddDTO.wishlistName());
        wishlist.setUserId(userModel.getUserId());

        wishlistRepository.save(wishlist);

        return new WishlistDTO(
                wishlist.getWishlistId(),
                wishlist.getWishlistName(),
                wishlist.getUserId());
    }

    @Transactional
    public WishlistDTO getWishlist(Long wishlistId, String keycloakId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!wishlist.getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        return new WishlistDTO(
          wishlist.getWishlistId(),
          wishlist.getWishlistName(),
          wishlist.getUserId()
        );
    }

    @Transactional
    public void deleteWishlist(Long wishlistId, String keycloakId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!wishlist.getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        wishlistRepository.deleteById(wishlistId);
    }

    @Transactional
    public WishlistItemDTO addWishlistItem(WishlistItemAddDTO wishlistItemAddDTO, String keycloakId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistItemAddDTO.wishlistId())
                .orElseThrow(() -> new IllegalArgumentException("Wishlist doesn't exist!"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!wishlist.getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        if (!productRepository.existsById(wishlistItemAddDTO.productId())) {
            throw new IllegalArgumentException("Product doesn't exist!");
        }

        boolean exists = wishlistItemRepository.existsByWishlistModelAndProductId(wishlist, wishlistItemAddDTO.productId());
        if (exists) {
            throw new IllegalStateException("Wishlist item already exists!");
        }

        WishlistItemModel wishlistItemModel = new WishlistItemModel();
        wishlistItemModel.setProductId(wishlistItemAddDTO.productId());
        wishlistItemModel.setCreatedAt(LocalDateTime.now());

        wishlist.addItem(wishlistItemModel);

        wishlistItemRepository.save(wishlistItemModel);

        return new WishlistItemDTO(
                wishlistItemModel.getWishlistItemId(),
                wishlistItemModel.getWishlistModel().getWishlistId(),
                wishlistItemModel.getProductId(),
                wishlistItemModel.getCreatedAt()
        );
    }

    @Transactional
    public WishlistItemDTO getWishlistItem(Long wishlistItemId, String keycloakId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item doesn't exist!"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!wishlistItem.getWishlistModel().getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User is not authorized to access wishlist item");
        }

        return new WishlistItemDTO(
                wishlistItem.getWishlistItemId(),
                wishlistItem.getWishlistModel().getWishlistId(),
                wishlistItem.getProductId(),
                wishlistItem.getCreatedAt()
        );
    }

    @Transactional
    public List<WishlistItemDTO> getWishlistItems(String name, String keycloakId) {
        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        WishlistModel wishlistModel = wishlistRepository.findByUserIdAndWishlistName(userModel.getUserId(), name)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        List<WishlistItemModel> wishlistItems = wishlistModel.getItems();

        return wishlistItems.stream()
                .map(item -> new WishlistItemDTO(
                        item.getWishlistItemId(),
                        item.getWishlistModel().getWishlistId(),
                        item.getProductId(),
                        item.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWishlistItem(Long wishlistItemId, String keycloakId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findByWishlistItemId(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item doesn't exist!"));

        WishlistModel wishlist = wishlistItem.getWishlistModel();
        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!wishlist.getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User not authorized to delete wishlist item");
        }

        wishlist.removeItem(wishlistItem);

        wishlistItemRepository.delete(wishlistItem);
    }

    @Transactional
    public ReviewDTO addReview(ReviewAddDTO reviewAddDTO, String keycloakId) {
        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ProductModel productModel = productRepository.findById(reviewAddDTO.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean exists = reviewRepository.existsByUserModelAndProductModel(userModel, productModel);
        if (exists) {
            throw new IllegalStateException("Review already exists!");
        }

        ReviewModel reviewModel = new ReviewModel();
        reviewModel.setDescription(reviewAddDTO.description());
        reviewModel.setTitle(reviewAddDTO.title());
        reviewModel.setRatingsEnum(reviewAddDTO.reviewRatingsEnum());

        userModel.addReview(reviewModel);
        productModel.addReview(reviewModel);

        reviewRepository.save(reviewModel);

        return new ReviewDTO(
                reviewModel.getReviewId(),
                userModel.getUserId(),
                productModel.getProductId(),
                reviewModel.getTitle(),
                reviewModel.getDescription(),
                reviewModel.getRatingsEnum()
        );
    }

    @Transactional
    public ReviewDTO getReviewById(Long reviewId) {
        ReviewModel reviewModel = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        return new ReviewDTO(
                reviewModel.getReviewId(),
                reviewModel.getUserModel().getUserId(),
                reviewModel.getProductModel().getProductId(),
                reviewModel.getTitle(),
                reviewModel.getDescription(),
                reviewModel.getRatingsEnum()
        );
    }

    @Transactional
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userModel.getReviewModels()
            .stream().map(item -> new ReviewDTO(
                    item.getReviewId(),
                    item.getUserModel().getUserId(),
                    item.getProductModel().getProductId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getRatingsEnum()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return productModel.getReviewModels()
            .stream().map(item -> new ReviewDTO(
                    item.getReviewId(),
                    item.getUserModel().getUserId(),
                    item.getProductModel().getProductId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getRatingsEnum()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId, String keycloakId) {
        ReviewModel reviewModel = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User doesn't exist!"));

        if (!reviewModel.getUserModel().getUserId().equals(userModel.getUserId())) {
            throw new SecurityException("User not authorized to delete review");
        }

        userModel.removeReview(reviewModel);

        ProductModel productModel = reviewModel.getProductModel();
        productModel.removeReview(reviewModel);

        reviewRepository.delete(reviewModel);
    }

    @Transactional
    public UserDTO updateUserData(UserUpdateDTO userUpdateDTO, String keycloakId) {
        UserModel userModel = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userUpdateDTO.address() != null)
            userModel.setAddress(userUpdateDTO.address());
        if (userUpdateDTO.phone() != null)
            userModel.setPhone(userUpdateDTO.phone());
        userRepository.save(userModel);

        return new UserDTO(userModel.getUserId(),
                userModel.getUsername(),
                userModel.getEmail(),
                userModel.getAddress(),
                userModel.getPhone());
    }

    public Boolean checkIfUserExists(String keycloakId) {
        return userRepository.existsByKeycloakId(keycloakId);
    }

    @Transactional
    public void addUser(AddUserDto dto) {
        UserModel userModel = UserModel.builder()
                .keycloakId(dto.keycloakId())
                .username(dto.username())
                .email(dto.email())
                .role(dto.role())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .build();

        userRepository.save(userModel);
    }

    public Optional<UserModel> findUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }

    @Transactional
    public void deleteUsersTable() {
        userRepository.deleteAll();
    }
}
