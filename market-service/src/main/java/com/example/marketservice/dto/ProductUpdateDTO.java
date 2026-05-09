package com.example.marketservice.dto;

public record ProductUpdateDTO(String productName,
                               Float price,
                               String description,
                               Integer quantity) {
}
