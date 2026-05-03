package com.example.marketservice.dto.WishlistItemDTOs;

public record WishlistItemAddDTO(Long wishlistId,
                                 Long userId,
                                 Long productId) {
}
