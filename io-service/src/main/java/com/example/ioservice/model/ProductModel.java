package com.example.ioservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    private String productName;
    private Float price;
    private String description;
    private Long ownerId;
    private Integer quantity;

    @OneToMany(mappedBy = "productModel", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReviewModel> reviewModels = new ArrayList<>();

    public void addReview(ReviewModel reviewModel) {
        reviewModels.add(reviewModel);
        reviewModel.setProductModel(this);
    }

    public void removeReview(ReviewModel reviewModel) {
        reviewModels.remove(reviewModel);
        reviewModel.setProductModel(null);
    }
}
