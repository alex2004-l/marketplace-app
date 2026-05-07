package com.example.ioservice.repository;

import com.example.ioservice.model.CartProductModel;
import com.example.ioservice.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    Optional<OrderModel> findByCartIdAndStatus(Long cartId, String status);
    List<OrderModel> findAllByUserId(Long userId);
}
