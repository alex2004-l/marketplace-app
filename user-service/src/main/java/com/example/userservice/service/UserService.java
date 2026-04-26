package com.example.userservice.service;

import com.example.userservice.model.Role;
import com.example.userservice.model.UserModel;
import com.example.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void addUser(String keycloakId, String username, String email, String firstName, String lastName, String role) {
        if (userRepository.existsByKeycloakId(keycloakId)) {
            log.info("User {} already exists in database. Skipping sync.", username);
            return;
        }

        Role mappedRole = (role != null && role.toUpperCase().contains("ADMIN")) ? Role.ADMIN : Role.USER;
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
}
