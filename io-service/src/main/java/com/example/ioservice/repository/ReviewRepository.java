package com.example.ioservice.repository;

import com.example.ioservice.model.ProductModel;
import com.example.ioservice.model.ReviewModel;
import com.example.ioservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewModel, Long> {
    Optional<ReviewModel> findByUserModel_UserIdAndProductModel_ProductId(Long userId, Long productId);
    boolean existsByUserModelAndProductModel(UserModel userModel, ProductModel productModel);
}
