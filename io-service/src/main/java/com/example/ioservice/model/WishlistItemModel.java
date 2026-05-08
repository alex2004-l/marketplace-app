package com.example.ioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_item")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WishlistItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistItemId;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishlistModel wishlistModel;

    private Long productId;
    private LocalDateTime createdAt;
}
