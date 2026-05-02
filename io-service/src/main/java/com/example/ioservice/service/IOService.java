package com.example.ioservice.service;

import com.example.ioservice.dto.AddProductToCartDto;
import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.RemoveProductFromCartDto;
import com.example.ioservice.dto.UserDto;
import com.example.ioservice.model.CartModel;
import com.example.ioservice.model.CartProductModel;
import com.example.ioservice.model.ProductModel;
import com.example.ioservice.model.UserModel;
import com.example.ioservice.repository.CartProductRepository;
import com.example.ioservice.repository.CartRepository;
import com.example.ioservice.repository.ProductRepository;
import com.example.ioservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IOService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

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
            cartProductModel.setQuantity(cartProductModel.getQuantity() + addProductToCartDto.quantity());
            cartProductRepository.save(cartProductModel);
        } else {
            CartProductModel newCartProduct = new CartProductModel();
            newCartProduct.setCartId(cart.getCartId());
            newCartProduct.setProductId(addProductToCartDto.productId());
            newCartProduct.setQuantity(addProductToCartDto.quantity());
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
