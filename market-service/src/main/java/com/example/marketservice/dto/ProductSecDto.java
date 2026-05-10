package com.example.marketservice.dto;

public record ProductSecDto(Long productId, String productName, Float price, String description, String keycloakId, Integer quantity) {
}
