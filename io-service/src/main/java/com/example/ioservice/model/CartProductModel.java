package com.example.ioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "cartProduct")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cartId;
    private Long productId;
    private Long quantity;
}
