package com.example.ioservice.dto;

public record ProductUpdateDTO(String productName,
                               Float price,
                               String description,
                               Integer quantity) {
}
