package com.example.ioservice.dto.ReviewDTOs;

import com.example.ioservice.enums.ReviewRatingsEnum;

public record ReviewAddDTO(Long userId,
                           Long productId,
                           String title,
                           String description,
                           ReviewRatingsEnum reviewRatingsEnum) {
}
