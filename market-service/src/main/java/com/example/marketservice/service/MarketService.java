package com.example.marketservice.service;

import com.example.marketservice.dto.ProductDto;
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
}
