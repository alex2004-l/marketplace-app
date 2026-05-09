package com.example.marketservice.dto.ReviewDTOs;

import com.example.marketservice.enums.ReviewRatingsEnum;

public record ReviewAddDTO(Long productId,
                           String title,
                           String description,
                           ReviewRatingsEnum reviewRatingsEnum) {
}
