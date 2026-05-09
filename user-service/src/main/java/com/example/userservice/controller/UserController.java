package com.example.userservice.controller;

import com.example.userservice.dto.UserDTOs.UserUpdateDTO;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/test_user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> startCheck() {
        return ResponseEntity.ok("User service working\n");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> loginUser() {
        return ResponseEntity.ok("Successful login for user!\n");
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('client_seller')")
    public ResponseEntity<String> loginSeller() {
        return ResponseEntity.ok("Successful login for seller!\n");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> loginAdmin() {
        return ResponseEntity.ok("Successful login for admin!\n");
    }

    @PostMapping("/user/update")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> updateUserData(@RequestBody UserUpdateDTO userDTO,  @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return userService.updateUserData(userDTO, keycloakSub);
    }
}
