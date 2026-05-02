package com.example.marketservice.dto;

public record AddProductToCartDto(Long userId, Long productId, Long quantity) {
}
