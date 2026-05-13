package com.example.marketservice.dto;

import java.util.List;

public record CartDto(Float total, List<OrderedProductDto> products) {
}
