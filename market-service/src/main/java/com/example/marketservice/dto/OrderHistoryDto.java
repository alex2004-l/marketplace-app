package com.example.marketservice.dto;

import java.util.List;

public record OrderHistoryDto(Long orderId, Float total, String status, List<OrderedProductDto> products) {
}
