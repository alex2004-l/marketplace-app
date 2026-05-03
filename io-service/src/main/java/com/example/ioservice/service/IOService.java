package com.example.ioservice.service;

import com.example.ioservice.dto.*;
import com.example.ioservice.model.*;
import com.example.ioservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IOService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final OrderRepository orderRepository;

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
}
