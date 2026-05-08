package com.example.marketservice.dto.WishlistDTOs;

public record WishlistDTO(Long wishlistId,
                          String wishlistName,
                          Long userId) {
}