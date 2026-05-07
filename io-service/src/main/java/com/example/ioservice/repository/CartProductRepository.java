package com.example.ioservice.repository;

import com.example.ioservice.model.CartModel;
import com.example.ioservice.model.CartProductModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProductModel, Long> {
    Optional<CartProductModel> findByCartIdAndProductId(Long cartId, Long productId);
    List<CartProductModel> findAllByCartId(Long cartId);
    @Transactional
    void deleteAllByCartId(Long cartId);
    @Transactional
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
