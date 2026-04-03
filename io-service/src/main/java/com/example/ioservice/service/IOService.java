package com.example.ioservice.service;

import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.UserDto;
import com.example.ioservice.model.ProductModel;
import com.example.ioservice.model.UserModel;
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

    public ProductDto addProduct(ProductDto productDto) {
        ProductModel productModel = new ProductModel(productDto.productId(), productDto.productName(),
                productDto.price(), productDto.description(), productDto.ownerId(), productDto.quantity());
        productRepository.save(productModel);

        return new ProductDto(productModel.getProductId(), productModel.getProductName(), productModel.getPrice(),
                productModel.getDescription(), productModel.getOwnerId(), productModel.getQuantity());
    }

    public List<ProductDto> seeAllProducts() {
        return productRepository.findAll().stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getOwnerId(),
                        productModel.getQuantity())).toList();
    }

    public ProductDto getProductById(Long productId) {
        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        return productModelOptional.map(productModel -> new ProductDto(productModel.getProductId(),
                productModel.getProductName(), productModel.getPrice(), productModel.getDescription(),
                productModel.getOwnerId(), productModel.getQuantity())).orElseThrow(() -> new RuntimeException("No product with id " + productId + "!"));
    }

    public List<ProductDto> searchByName(String name) {
        return productRepository.findByProductNameIgnoreCaseStartingWith(name.toLowerCase()).stream()
                .map(productModel -> new ProductDto(productModel.getProductId(), productModel.getProductName(),
                        productModel.getPrice(), productModel.getDescription(), productModel.getOwnerId(),
                        productModel.getQuantity())).toList();
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
