package com.example.ioservice.repository;

import com.example.ioservice.model.CartModel;
import com.example.ioservice.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartModel, Long> {
    Optional<CartModel> findByUserIdAndStatus(Long userId, String status);
}
