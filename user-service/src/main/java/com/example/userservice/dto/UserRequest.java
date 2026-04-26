package com.example.userservice.dto;

public record UserRequest(String username, String email, String role, String firstName, String lastName) {
}