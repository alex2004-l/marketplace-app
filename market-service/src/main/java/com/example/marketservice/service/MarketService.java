package com.example.marketservice.service;

import com.example.marketservice.dto.ProductDto;
import com.example.marketservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.marketservice.dto.WishlistDTOs.WishlistDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.marketservice.feignclient.IOClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final IOClient ioClient;

    public ResponseEntity<ProductDto> addProduct(ProductDto productDto) {
        if (productDto.productName().length() <= 3) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        if (productDto.price() <= 0) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        return ResponseEntity.ok(ioClient.addProduct(productDto));
    }

    public List<ProductDto> seeAllProducts() {
        return ioClient.seeAllProducts();
    }

    public ResponseEntity<ProductDto> getProductById(Long id) {
        try {
            return ResponseEntity.ok(ioClient.getProductById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProductDto(null, null,null, null,null,null));
        }
    }

    public List<ProductDto> searchByName(String name) {
        return ioClient.searchByName(name);
    }

    public ResponseEntity<?> addWishlist(WishlistAddDTO wishlistAddDTO) {
        try {
            return ResponseEntity.ok(ioClient.addWishlist(wishlistAddDTO));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistById(Long id, Long userId) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistById(id, userId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteWishlist(Long id, Long userId) {
        try {
            ioClient.deleteWishlist(id, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted wishlist");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> addWishlistItem(WishlistItemAddDTO wishlistItemAddDTO) {
        try {
            return ResponseEntity.ok(ioClient.addWishlistItem(wishlistItemAddDTO));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistItemById(Long id, Long userId) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistItemById(id, userId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistItems(String name, Long userId) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistItems(name, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteWishlistItem(Long id, Long userId) {
        try {
            ioClient.deleteWishlistItem(id, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted wishlist item");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
