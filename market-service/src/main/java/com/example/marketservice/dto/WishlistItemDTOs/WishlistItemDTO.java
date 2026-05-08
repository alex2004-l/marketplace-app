package com.example.marketservice.dto.WishlistItemDTOs;

import java.time.LocalDateTime;

public record WishlistItemDTO(Long wishlistItemId,
                              Long wishlistId,
                              Long productId,
                              LocalDateTime createdAt) {
}
