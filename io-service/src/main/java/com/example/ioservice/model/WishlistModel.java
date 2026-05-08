package com.example.ioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlist")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WishlistModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long wishlistId;

    private String wishlistName;
    private Long userId;

    @OneToMany(mappedBy = "wishlistModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItemModel> items = new ArrayList<>();

    public void addItem(WishlistItemModel wishlistItemModel) {
        items.add(wishlistItemModel);
        wishlistItemModel.setWishlistModel(this);
    }

    public void removeItem(WishlistItemModel wishlistItemModel) {
        items.remove(wishlistItemModel);
        wishlistItemModel.setWishlistModel(null);
    }
}
