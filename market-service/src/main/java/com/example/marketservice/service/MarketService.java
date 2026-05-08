package com.example.marketservice.service;

import com.example.marketservice.dto.*;
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

    public ResponseEntity<?> addReview(ReviewAddDTO reviewAddDTO) {
        try {
            return ResponseEntity.ok(ioClient.addReview(reviewAddDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getReviewById(Long reviewId) {
        try {
            return ResponseEntity.ok(ioClient.getReviewById(reviewId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getReviewsByUserId(Long userId) {
        try {
            return ResponseEntity.ok(ioClient.getReviewsByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getReviewsByProductId(Long productId) {
        try {
            return ResponseEntity.ok(ioClient.getReviewsByProductId(productId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteReview (Long reviewId, Long userId) {
        try {
            ioClient.deleteReview(reviewId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted review");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    public String addProductToCart(AddProductToCartDto dto) {
        return ioClient.addProductToCart(dto);
    }

    public String removeOneProduct(RemoveProductFromCartDto dto) {
        return ioClient.removeOneProduct(dto);
    }

    public String removeProduct(RemoveProductFromCartDto dto) {
        return ioClient.removeProduct(dto);
    }

    public Float getCartTotal(String userId) {
        return ioClient.getCartTotal(userId);
    }

    public String makeOrder(OrderDto orderDto) {
        return ioClient.makeOrder(orderDto);
    }

    public List<OrderHistoryDto> getOrdersHistory(String userId) {
        return ioClient.getOrdersHistory(userId);
    }
}
