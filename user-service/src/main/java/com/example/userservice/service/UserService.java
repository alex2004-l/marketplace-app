package com.example.userservice.service;

import com.example.userservice.dto.UserDTOs.AddUserDto;
import com.example.userservice.dto.UserDTOs.UserDTO;
import com.example.userservice.dto.UserDTOs.UserUpdateDTO;
import com.example.userservice.feignClient.IOClient;
import com.example.userservice.model.Role;
import com.example.userservice.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final IOClient ioClient;

    public void addUser(String keycloakId, String username, String email, String firstName, String lastName, String role) {
        if (ioClient.checkIfUserExists(keycloakId)) {
            log.info("User {} already exists in database. Skipping sync.", username);
            return;
        }

        Role mappedRole = Role.USER;
        if (role != null && role.toUpperCase().contains("ADMIN"))
            mappedRole = Role.ADMIN;
        else if (role != null && role.toUpperCase().contains("SELLER"))
            mappedRole = Role.SELLER;


        AddUserDto dto = new AddUserDto(keycloakId, email, username, mappedRole,
                firstName, lastName);
        ioClient.addUser(dto);
        log.info("Synced new user: {} (ID: {})", username, keycloakId);
    }

    public Collection<? extends GrantedAuthority> setRoles(String keycloakId) {
        Optional<UserModel> userModel = ioClient.findUserByKeycloakId(keycloakId);
        return userModel
                .map(user -> List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .orElse(List.of());
    }

    public void deleteUsersTable() {
        log.warn("Deleting all the users from the database");
        ioClient.deleteUsersTable();
    }

    public ResponseEntity<?> updateUserData(UserUpdateDTO userUpdateDTO, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.updateUserData(userUpdateDTO, keycloakSub));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
