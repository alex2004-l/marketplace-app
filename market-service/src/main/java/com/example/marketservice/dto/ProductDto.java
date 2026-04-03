package com.example.marketservice.dto;

public record ProductDto(Long productId, String productName, Float price, String description, Long ownerId, Integer quantity) {
}
