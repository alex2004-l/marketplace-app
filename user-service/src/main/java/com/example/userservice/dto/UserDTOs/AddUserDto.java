package com.example.userservice.dto.UserDTOs;

import com.example.userservice.model.Role;

public record AddUserDto(String keycloakId, String email, String username, Role role,
                         String firstName, String lastName) {
}
