package com.example.marketservice.dto;

public record OrderedProductDto(Long productId, String productName, Float price, Long quantity) {
}
