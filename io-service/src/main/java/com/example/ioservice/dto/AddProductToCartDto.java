package com.example.ioservice.dto;

public record AddProductToCartDto(String keycloakId, Long productId, Long quantity) {
}
