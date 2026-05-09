package com.example.userservice.dto.UserDTOs;

public record UserDTO(Long userId,
                      String username,
                      String email,
                      String address,
                      String phone) {
}