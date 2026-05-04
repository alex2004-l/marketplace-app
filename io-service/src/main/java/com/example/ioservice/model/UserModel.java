package com.example.ioservice.model;

import com.example.ioservice.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    @OneToMany(mappedBy = "userModel", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReviewModel> reviewModels = new ArrayList<>();

    public void addReview(ReviewModel reviewModel) {
        reviewModels.add(reviewModel);
        reviewModel.setUserModel(this);
    }

    public void removeReview(ReviewModel reviewModel) {
        reviewModels.remove(reviewModel);
        reviewModel.setUserModel(null);
    }
}
