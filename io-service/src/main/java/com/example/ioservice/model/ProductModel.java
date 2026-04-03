package com.example.ioservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private Float price;
    private String description;
    private Long ownerId;
    private Integer quantity;
}
