package com.example.marketservice.dto.ReviewDTOs;

import com.example.marketservice.enums.ReviewRatingsEnum;

public record ReviewDTO(Long reviewId,
                        Long userId,
                        Long productId,
                        String title,
                        String description,
                        ReviewRatingsEnum reviewRatingsEnum) {
}
