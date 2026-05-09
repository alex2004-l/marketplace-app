package com.example.ioservice.dto.UserDTOs;

public record UserDTO(Long userId,
                      String username,
                      String email,
                      String address,
                      String phone) {}
