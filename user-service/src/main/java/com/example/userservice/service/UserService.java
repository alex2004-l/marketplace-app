package com.example.userservice.service;

import com.example.userservice.dto.UserDTOs.UserDTO;
import com.example.userservice.dto.UserDTOs.UserUpdateDTO;
import com.example.userservice.feignClient.IOClient;
import com.example.userservice.model.Role;
import com.example.userservice.model.UserModel;
import com.example.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final IOClient ioClient;

    @Transactional
    public void addUser(String keycloakId, String username, String email, String firstName, String lastName, String role) {
        if (userRepository.existsByKeycloakId(keycloakId)) {
            log.info("User {} already exists in database. Skipping sync.", username);
            return;
        }

        Role mappedRole = Role.USER;
        if (role != null && role.toUpperCase().contains("ADMIN"))
            mappedRole = Role.ADMIN;
        else if (role != null && role.toUpperCase().contains("SELLER"))
            mappedRole = Role.SELLER;

        UserModel userModel = UserModel.builder()
                        .keycloakId(keycloakId)
                        .email(email)
                        .username(username)
                        .role(mappedRole)
                        .firstName(firstName)
                        .lastName(lastName)
                        .build();

        userRepository.save(userModel);
        log.info("Synced new user: {} (ID: {})", username, keycloakId);
    }

    public Collection<? extends GrantedAuthority> setRoles(String keycloakId) {
        return userRepository
                .findByKeycloakId(keycloakId)
                .map(user -> List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .orElse(List.of());
    }

    @Transactional
    public void deleteUsersTable() {
        log.warn("Deleting all the users from the database");
        userRepository.deleteAll();
    }

    @Transactional
    public ResponseEntity<?> updateUserData(UserUpdateDTO userUpdateDTO) {
        try {
            return ResponseEntity.ok(ioClient.updateUserData(userUpdateDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
