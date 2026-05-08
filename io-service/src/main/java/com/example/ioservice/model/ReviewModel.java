package com.example.ioservice.model;

import com.example.ioservice.enums.ReviewRatingsEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ReviewModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ReviewRatingsEnum ratingsEnum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductModel productModel;
}
