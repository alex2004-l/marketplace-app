package com.example.marketservice.service;

import com.example.marketservice.dto.AddProductToCartDto;
import com.example.marketservice.dto.ProductDto;
import com.example.marketservice.dto.RemoveProductFromCartDto;
import com.example.marketservice.feignclient.IOClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final IOClient ioClient;

    public ResponseEntity<ProductDto> addProduct(ProductDto productDto) {
        if (productDto.productName().length() <= 3) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        if (productDto.price() <= 0) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        return ResponseEntity.ok(ioClient.addProduct(productDto));
    }

    public List<ProductDto> seeAllProducts() {
        return ioClient.seeAllProducts();
    }

    public ResponseEntity<ProductDto> getProductById(Long id) {
        try {
            return ResponseEntity.ok(ioClient.getProductById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProductDto(null, null,null, null,null,null));
        }
    }

    public List<ProductDto> searchByName(String name) {
        return ioClient.searchByName(name);
    }

    public String addProductToCart(AddProductToCartDto dto) {
        return ioClient.addProductToCart(dto);
    }

    public String removeOneProduct(RemoveProductFromCartDto dto) {
        return ioClient.removeOneProduct(dto);
    }

    public String removeProduct(RemoveProductFromCartDto dto) {
        return ioClient.removeProduct(dto);
    }

    public Float getCartTotal(String userId) {
        return ioClient.getCartTotal(userId);
    }
}
