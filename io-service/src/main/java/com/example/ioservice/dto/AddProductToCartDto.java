package com.example.ioservice.dto;

public record AddProductToCartDto(Long userId, Long productId, Long quantity) {
}
