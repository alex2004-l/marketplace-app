package com.example.ioservice.dto.UserDTOs;

public record UserUpdateDTO(Long userId,
                            String address,
                            String phone) {
}
