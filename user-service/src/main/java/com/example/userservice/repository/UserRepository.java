package com.example.userservice.repository;

import com.example.userservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByKeycloakId(String keycloakId);
}
