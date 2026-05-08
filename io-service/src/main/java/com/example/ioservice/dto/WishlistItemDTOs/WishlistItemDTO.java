package com.example.ioservice.dto.WishlistItemDTOs;

import java.time.LocalDateTime;

public record WishlistItemDTO(Long wishlistItemId,
                              Long wishlistId,
                              Long productId,
                              LocalDateTime createdAt) {
}
