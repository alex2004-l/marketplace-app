package com.example.marketservice.controller;

import com.example.marketservice.dto.*;
import com.example.marketservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.marketservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.marketservice.service.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@AllArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @GetMapping("/test_market")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> startCheck() {
        return ResponseEntity.ok("Market service working");
    }

    @PostMapping("/add_product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto) {
        return marketService.addProduct(productDto);
    }

    @GetMapping("/see_all_products")
    List<ProductDto> seeAllProducts() {
        return marketService.seeAllProducts();
    }

    @GetMapping("/product/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        return marketService.getProductById(id);
    }

    @PutMapping("/product/{id}")
    @PreAuthorize("hasRole('client_seller')")
    public  ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody ProductUpdateDTO productUpdateDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.updateProduct(id, productUpdateDTO, keycloakSub);
    }

    @DeleteMapping("/product/{id}")
    @PreAuthorize("hasRole('client_seller')")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.deleteProduct(productId, keycloakSub);
    }

    @GetMapping("/search")
    ResponseEntity<List<ProductDto>> searchByName(@SpringQueryMap SearchProductDTO searchProductDTO) {
        return ResponseEntity.ok(marketService.searchByName(searchProductDTO));
    }

    @PostMapping("/wishlist/add")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.addWishlist(wishlistAddDTO, keycloakSub);
    }

    @GetMapping("/wishlist/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> getWishlistById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.getWishlistById(id, keycloakSub);
    }

    @DeleteMapping("/wishlist/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> deleteWishlist(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.deleteWishlist(id, keycloakSub);
    }

    @PostMapping("/wishlist-item/add")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.addWishlistItem(wishlistItemAddDTO, keycloakSub);
    }

    @GetMapping("/wishlist-item/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> getWishlistItemById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.getWishlistItemById(id, keycloakSub);
    }

    @GetMapping("/wishlist/name/{name}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> getWishlistItems(@PathVariable("name") String name, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.getWishlistItems(name, keycloakSub);
    }

    @DeleteMapping("/wishlist-item/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> deleteWishlistItem(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.deleteWishlistItem(id, keycloakSub);
    }

    @PostMapping("/review/add")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<?> addReview(@RequestBody ReviewAddDTO reviewAddDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.addReview(reviewAddDTO, keycloakSub);
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        return marketService.getReviewById(id);
    }

    @GetMapping("/review/user/{id}")
    public ResponseEntity<?> getReviewsByUserId(@PathVariable("id") Long id) {
        return marketService.getReviewsByUserId(id);
    }

    @GetMapping("/review/product/{id}")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable("id") Long id) {
        return marketService.getReviewsByProductId(id);
    }

    @DeleteMapping("/review/{id}")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getClaimAsString("sub");
        return marketService.deleteReview(id, keycloakSub);
    }

    @PostMapping("/add_product_to_cart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addProductToCart(@RequestBody AddProductToCartDto addProductToCartDto) {
        return ResponseEntity.ok(marketService.addProductToCart(addProductToCartDto));
    }

    @PutMapping("/remove_one_product_from_cart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeOneProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto) {
        return ResponseEntity.ok(marketService.removeOneProduct(removeProductFromCartDto));
    }

    @PostMapping("/remove_product_from_cart")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto) {
        return ResponseEntity.ok(marketService.removeProduct(removeProductFromCartDto));
    }

    @GetMapping("/get_cart_total/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Float getCartTotal(@PathVariable("userId") String userId) {
        return marketService.getCartTotal(userId);
    }

    @PostMapping("/make_order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> makeOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(marketService.makeOrder(orderDto));
    }

    @GetMapping("/get_orders_history/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderHistoryDto> getOrdersHistory(@PathVariable("userId") String userId) {
        return marketService.getOrdersHistory(userId);
    }
}
