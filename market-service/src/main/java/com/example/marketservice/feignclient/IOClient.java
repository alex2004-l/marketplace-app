package com.example.marketservice.feignclient;

import com.example.marketservice.dto.*;
import com.example.marketservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.marketservice.dto.ReviewDTOs.ReviewDTO;
import com.example.marketservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.marketservice.dto.WishlistDTOs.WishlistDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.marketservice.dto.WishlistItemDTOs.WishlistItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "io-service", url = "http://io-service:8080/internal/io")
public interface IOClient {
    @PostMapping("/add_product")
    ProductDto addProduct(@RequestBody ProductDto productDto);

    @GetMapping("/see_all_products")
    List<ProductDto> seeAllProducts();

    @GetMapping("/product/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PutMapping("/product/{id}")
    ProductDto updateProduct(@PathVariable("id") Long id, @RequestBody ProductUpdateDTO productUpdateDTO, @RequestParam("keycloakId") String keycloakId);

    @DeleteMapping("/product/{id}")
    void deleteProduct(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/search")
    List<ProductDto> searchByName(@SpringQueryMap SearchProductDTO searchProductDTO);

    @PostMapping("/wishlist/add")
    WishlistDTO addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/wishlist/{id}")
    WishlistDTO getWishlistById(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @DeleteMapping("/wishlist/{id}")
    void deleteWishlist(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @PostMapping("/wishlist-item/add")
    WishlistItemDTO addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/wishlist-item/{id}")
    WishlistItemDTO getWishlistItemById(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/wishlist/name/{name}")
    List<WishlistItemDTO> getWishlistItems(@PathVariable("name") String name, @RequestParam("keycloakId") String keycloakId);

    @DeleteMapping("/wishlist-item/{id}")
    void deleteWishlistItem(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @PostMapping("/review/add")
    ReviewDTO addReview(@RequestBody ReviewAddDTO reviewAddDTO, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/review/{id}")
    ReviewDTO getReviewById(@PathVariable("id") Long id);

    @GetMapping("/review/user/{id}")
    List<ReviewDTO> getReviewsByUserId(@PathVariable("id") Long id);

    @GetMapping("/review/product/{id}")
    List<ReviewDTO> getReviewsByProductId(@PathVariable("id") Long id);

    @DeleteMapping("/review/{id}")
    void deleteReview(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId);

    @PostMapping("/add_product_to_cart")
    String addProductToCart(@RequestBody AddProductToCartDto addProductToCartDto);

    @PutMapping("/remove_one_product_from_cart")
    String removeOneProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto);

    @PostMapping("/remove_product_from_cart")
    String removeProduct(RemoveProductFromCartDto removeProductFromCartDto);

    @GetMapping("/get_cart_total/{userId}")
    Float getCartTotal(@PathVariable("userId") String userId);

    @PostMapping("/make_order")
    String makeOrder(@RequestBody OrderDto orderDto);

    @GetMapping("/get_orders_history/{userId}")
    List<OrderHistoryDto> getOrdersHistory(@PathVariable String userId);
}
