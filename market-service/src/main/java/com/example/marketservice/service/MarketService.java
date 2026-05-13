package com.example.marketservice.service;

import com.example.marketservice.dto.*;
import com.example.marketservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.marketservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.marketservice.feignclient.IOClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final IOClient ioClient;
    private final JwtDecoder jwtDecoderByJwkKeySetUri;

    public ResponseEntity<ProductDto> addProduct(ProductSecDto productDto) {
        if (productDto.productName().length() <= 3) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        if (productDto.price() <= 0) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductDto(null, null,null, null,null,null));
        ProductDto newProduct = ioClient.addProduct(productDto);
        return ResponseEntity.ok(newProduct);
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

    public ResponseEntity<?> updateProduct(Long productId, ProductUpdateDTO productUpdateDTO, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.updateProduct(productId, productUpdateDTO, keycloakSub));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteProduct(Long productId, String keycloakSub) {
        try {
            ioClient.deleteProduct(productId, keycloakSub);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted product");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public List<ProductDto> searchByName(SearchProductDTO searchProductDTO) {
        return ioClient.searchByName(searchProductDTO);
    }

    public ResponseEntity<?> addWishlist(WishlistAddDTO wishlistAddDTO, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.addWishlist(wishlistAddDTO, keycloakSub));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistById(Long id, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistById(id, keycloakSub));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteWishlist(Long id, String keycloakSub) {
        try {
            ioClient.deleteWishlist(id, keycloakSub);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted wishlist");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> addWishlistItem(WishlistItemAddDTO wishlistItemAddDTO, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.addWishlistItem(wishlistItemAddDTO, keycloakSub));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistItemById(Long id, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistItemById(id, keycloakSub));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getWishlistItems(String name, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.getWishlistItems(name, keycloakSub));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteWishlistItem(Long id, String keycloakSub) {
        try {
            ioClient.deleteWishlistItem(id, keycloakSub);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted wishlist item");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public ResponseEntity<?> addReview(ReviewAddDTO reviewAddDTO, String keycloakSub) {
        try {
            return ResponseEntity.ok(ioClient.addReview(reviewAddDTO, keycloakSub));
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

    public ResponseEntity<String> deleteReview (Long reviewId, String keycloakSub) {
        try {
            ioClient.deleteReview(reviewId, keycloakSub);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted review");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
        
    public String addProductToCart(AddProductToCartSecDto dto) {
        return ioClient.addProductToCart(dto);
    }

    public String removeOneProduct(RemoveProductFromCartSecDto dto) {
        return ioClient.removeOneProduct(dto);
    }

    public String removeProduct(RemoveProductFromCartSecDto dto) {
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
    public CartDto getCartProducts(String userId) {
        return ioClient.getCartProducts(userId);
    }
}
