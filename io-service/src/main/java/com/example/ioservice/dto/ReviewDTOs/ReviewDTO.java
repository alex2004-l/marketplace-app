package com.example.ioservice.dto.ReviewDTOs;

import com.example.ioservice.enums.ReviewRatingsEnum;

public record ReviewDTO(Long reviewId,
                        Long userId,
                        Long productId,
                        String title,
                        String description,
                        ReviewRatingsEnum reviewRatingsEnum) {
}
