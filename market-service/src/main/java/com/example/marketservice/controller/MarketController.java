package com.example.marketservice.controller;

import com.example.marketservice.dto.ProductDto;
import com.example.marketservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.marketservice.service.MarketService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/get/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        return marketService.getProductById(id);
    }

    @GetMapping("/search")
    List<ProductDto> searchByName(@RequestParam("name") String name) {
        return marketService.searchByName(name);
    }

    @PostMapping("/wishlist/add")
    public ResponseEntity<?> addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO) {
        return marketService.addWishlist(wishlistAddDTO);
    }

    @GetMapping("/wishlist/{id}")
    public ResponseEntity<?> getWishlistById(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return marketService.getWishlistById(id, userId);
    }

    @PostMapping("/wishlist/delete/{id}")
    public ResponseEntity<?> deleteWishlist(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return marketService.deleteWishlist(id, userId);
    }

    @PostMapping("/wishlist-item/add")
    public ResponseEntity<?> addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO) {
        return marketService.addWishlistItem(wishlistItemAddDTO);
    }

    @GetMapping("/wishlist-item/{id}")
    public ResponseEntity<?> getWishlistItemById(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return marketService.getWishlistItemById(id, userId);
    }

    @GetMapping("/wishlist/{name}")
    public ResponseEntity<?> getWishlistItems(@PathVariable("name") String name, @RequestParam("userId") Long userId) {
        return marketService.getWishlistItems(name, userId);
    }

    @PostMapping("/wishlist-item/delete/{id}")
    public ResponseEntity<?> deleteWishlistItem(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return marketService.deleteWishlistItem(id, userId);
    }
}
