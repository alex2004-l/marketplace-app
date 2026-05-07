package com.example.ioservice.dto;

import java.util.List;

public record OrderHistoryDto(Long orderId, Float total, String status, List<OrderedProductDto> products) {
}
