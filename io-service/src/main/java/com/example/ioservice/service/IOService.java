package com.example.ioservice.service;

import com.example.ioservice.dto.*;
import com.example.ioservice.model.*;
import com.example.ioservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

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

    public ProductDto addProduct(ProductDto productDto) {
        ProductModel productModel = new ProductModel(productDto.productId(), productDto.productName(),
                productDto.price(), productDto.description(), productDto.sellerId(), productDto.quantity());
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

    public List<ProductDto> searchByName(String name) {
        return productRepository.findByProductNameIgnoreCaseStartingWith(name.toLowerCase()).stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getSellerId(),
                        productModel.getQuantity())).toList();
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
        CartModel cart = cartRepository.findByUserIdAndStatus(addProductToCartDto.userId(), "inProgress")
                .orElseGet(() -> {
                   CartModel newCart = new CartModel();
                   newCart.setUserId(addProductToCartDto.userId());
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
        CartModel cartModel = cartRepository.findByUserIdAndStatus(dto.userId(), "inProgress")
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
        CartModel cartModel = cartRepository.findByUserIdAndStatus(dto.userId(), "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));
        cartProductRepository.deleteByCartIdAndProductId(cartModel.getCartId(), dto.productId());

        return "Product removed from cart!";
    }

    public Float getCartTotal(Long userId) {
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userId, "inProgress")
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
    public String makeOrder(Long userId) {
        CartModel cartModel = cartRepository.findByUserIdAndStatus(userId, "inProgress")
                .orElseThrow(() -> new RuntimeException("Cart doesn't exist!"));

        OrderModel orderModel = OrderModel.builder()
                .cartId(cartModel.getCartId())
                .price(getCartTotal(userId))
                .status("inProgress")
                .userId(userId)
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

    public List<OrderHistoryDto> getOrdersHistory(Long userId) {
        List<OrderModel> orders = orderRepository.findAllByUserId(userId);
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

    public String changeAddress(String username, String address) {
        Optional<UserModel> userModelOptional = Optional.ofNullable(userRepository.findByUsername(username));

        if (userModelOptional.isPresent()) {
            UserModel userModel = userModelOptional.get();
            userModel.setAddress(address);
            userRepository.save(userModel);
            return "Address changed successfully!";
        } else {
            return "User doesn't exist!";
        }
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Transactional
    public WishlistDTO addWishlist(WishlistAddDTO wishlistAddDTO) {
        boolean exists = wishlistRepository.existsByUserIdAndWishlistName(wishlistAddDTO.userId(), wishlistAddDTO.wishlistName());
        if (exists) {
            throw new IllegalStateException("Wishlist already exists!");
        }

        WishlistModel wishlist = new WishlistModel();
        wishlist.setWishlistName(wishlistAddDTO.wishlistName());
        wishlist.setUserId(wishlistAddDTO.userId());

        wishlistRepository.save(wishlist);

        return new WishlistDTO(
                wishlist.getWishlistId(),
                wishlist.getWishlistName(),
                wishlist.getUserId());
    }

    @Transactional
    public WishlistDTO getWishlist(Long wishlistId, Long userId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        return new WishlistDTO(
          wishlist.getWishlistId(),
          wishlist.getWishlistName(),
          wishlist.getUserId()
        );
    }

    @Transactional
    public void deleteWishlist(Long wishlistId, Long userId) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        wishlistRepository.deleteById(wishlistId);
    }

    @Transactional
    public WishlistItemDTO addWishlistItem(WishlistItemAddDTO wishlistItemAddDTO) {
        WishlistModel wishlist = wishlistRepository.findById(wishlistItemAddDTO.wishlistId())
                .orElseThrow(() -> new IllegalArgumentException("Wishlist doesn't exist!"));

        if (!wishlist.getUserId().equals(wishlistItemAddDTO.userId())) {
            throw new SecurityException("User is not authorized to access wishlist");
        }

        boolean product_exists = productRepository.existsById(wishlistItemAddDTO.productId());
        if (!product_exists) {
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
    public WishlistItemDTO getWishlistItem(Long wishlistItemId, Long userId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item doesn't exist!"));

        if (!wishlistItem.getWishlistModel().getUserId().equals(userId)) {
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
    public List<WishlistItemDTO> getWishlistItems(String name, Long userId) {
        WishlistModel wishlistModel = wishlistRepository.findByUserIdAndWishlistName(userId, name)
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
    public void deleteWishlistItem(Long wishlistItemId, Long userId) {
        WishlistItemModel wishlistItem = wishlistItemRepository.findByWishlistItemId(wishlistItemId);

        if (wishlistItem == null) {
            throw new IllegalArgumentException("Wishlist item doesn't exist!");
        }

        WishlistModel wishlist = wishlistItem.getWishlistModel();

        if (!wishlist.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete wishlist item");
        }

        wishlist.removeItem(wishlistItem);

        wishlistItemRepository.delete(wishlistItem);
    }

    @Transactional
    public ReviewDTO addReview(ReviewAddDTO reviewAddDTO) {
        List<UserModel> models = userRepository.findAll();

        System.out.println("Total users found: " + models.size());
        models.forEach(user -> System.out.println("User in DB: " + user));
        System.out.println(reviewAddDTO.userId());

        UserModel userModel = userRepository.findById(reviewAddDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ProductModel productModel = productRepository.findById(reviewAddDTO.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean exists = reviewRepository.existsByUserModelAndProductModel(
                        userModel, productModel);

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
    public void deleteReview(Long reviewId, Long userId) {
        ReviewModel reviewModel = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!reviewModel.getUserModel().getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete review");
        }

        UserModel userModel = reviewModel.getUserModel();
        userModel.removeReview(reviewModel);
        reviewRepository.delete(reviewModel);
    }
}
