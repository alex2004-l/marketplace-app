package com.example.userservice.model;

//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserModel {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String keycloakId;
    private String username;
    private String email;

//    @Enumerated(EnumType.STRING)
    private Role role;
    private String firstName;
    private String lastName;
    private String phone;

    private String address;
}
