package com.example.marketservice.dto;

public record AddProductToCartSecDto(String keycloakId, Long productId, Long quantity) {
}
