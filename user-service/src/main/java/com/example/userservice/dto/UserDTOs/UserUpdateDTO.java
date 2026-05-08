package com.example.userservice.dto.UserDTOs;

public record UserUpdateDTO(Long userId,
                            String address,
                            String phone) {
}
