package com.example.ioservice.dto.UserDTOs;

import com.example.ioservice.enums.Role;

public record AddUserDto(String keycloakId, String email, String username, Role role,
                         String firstName, String lastName) {
}
