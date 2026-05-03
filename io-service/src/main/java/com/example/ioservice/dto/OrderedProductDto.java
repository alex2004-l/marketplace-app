package com.example.ioservice.dto;

public record OrderedProductDto(Long productId, String productName, Float price, Long quantity) {
}
