package com.example.ioservice.repository;

import com.example.ioservice.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByProductNameIgnoreCaseStartingWith(String name, Sort sort);
}
