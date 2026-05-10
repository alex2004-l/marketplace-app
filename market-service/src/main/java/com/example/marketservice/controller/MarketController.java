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
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin') or hasRole('client_seller')")
    public ResponseEntity<String> startCheck() {
        return ResponseEntity.ok("Market service working");
    }

    @PostMapping("/add_product")
    @PreAuthorize("hasRole('client_seller') or hasRole('client_admin')")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto, @AuthenticationPrincipal Jwt jwt) {
        ProductSecDto productSecDto = new ProductSecDto(productDto.productId(), productDto.productName(), productDto.price(),
                productDto.description(), jwt.getClaimAsString("sub"), productDto.quantity());
        return marketService.addProduct(productSecDto);
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
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> addProductToCart(@RequestBody AddProductToCartDto addProductToCartDto, @AuthenticationPrincipal Jwt jwt) {
        AddProductToCartSecDto addProductToCartSecDto = new AddProductToCartSecDto(jwt.getClaimAsString("sub"),
                addProductToCartDto.productId(), addProductToCartDto.quantity());
        return ResponseEntity.ok(marketService.addProductToCart(addProductToCartSecDto));
    }

    @PutMapping("/remove_one_product_from_cart")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public ResponseEntity<String> removeOneProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto, @AuthenticationPrincipal Jwt jwt) {
        RemoveProductFromCartSecDto removeProductFromCartSecDto = new RemoveProductFromCartSecDto(jwt.getClaimAsString("sub"),
                removeProductFromCartDto.productId());
        return ResponseEntity.ok(marketService.removeOneProduct(removeProductFromCartSecDto));
    }

    @PostMapping("/remove_product_from_cart")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('client_user') or hasRole('client_admin')")
    public ResponseEntity<String> removeProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto, @AuthenticationPrincipal Jwt jwt) {
        RemoveProductFromCartSecDto removeProductFromCartSecDto = new RemoveProductFromCartSecDto(jwt.getClaimAsString("sub"),
                removeProductFromCartDto.productId());
        return ResponseEntity.ok(marketService.removeProduct(removeProductFromCartSecDto));
    }

    @GetMapping("/get_cart_total")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('client_user')")
    public Float getCartTotal(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        return marketService.getCartTotal(userId);
    }

    @PostMapping("/make_order")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> makeOrder(@AuthenticationPrincipal Jwt jwt) {
        OrderDto orderDto = new OrderDto(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(marketService.makeOrder(orderDto));
    }

    @GetMapping("/get_orders_history")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('client_user')")
    public List<OrderHistoryDto> getOrdersHistory(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        return marketService.getOrdersHistory(userId);
    }
}
