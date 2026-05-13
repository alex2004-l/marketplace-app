package com.example.ioservice.dto;

import java.util.List;

public record CartDto(Float total, List<OrderedProductDto> products) {
}
