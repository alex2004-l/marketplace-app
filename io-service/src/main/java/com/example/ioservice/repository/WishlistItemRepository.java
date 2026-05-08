package com.example.ioservice.repository;

import com.example.ioservice.model.WishlistItemModel;
import com.example.ioservice.model.WishlistModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItemModel, Long> {
    WishlistItemModel findByWishlistModelAndProductId(WishlistModel wishlistModel, Long productId);
    WishlistItemModel findByWishlistItemId(Long wishlistItemId);
    List<WishlistItemModel> findByWishlistModel(WishlistModel wishlistModel);
    boolean existsByWishlistModelAndProductId(WishlistModel wishlistModel, Long productId);
}
