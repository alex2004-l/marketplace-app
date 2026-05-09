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

    @GetMapping("/get/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @GetMapping("/search")
    public List<ProductDto> searchByName(@SpringQueryMap SearchProductDTO searchProductDTO);

    @PostMapping("/wishlist/add")
    public WishlistDTO addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO);

    @GetMapping("/wishlist/{id}")
    public WishlistDTO getWishlistById(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @PostMapping("/wishlist/delete/{id}")
    public void deleteWishlist(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @PostMapping("/wishlist-item/add")
    public WishlistItemDTO addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO);

    @GetMapping("/wishlist-item/{id}")
    public WishlistItemDTO getWishlistItemById(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @GetMapping("/wishlist/name/{name}")
    public List<WishlistItemDTO> getWishlistItems(@PathVariable("name") String name, @RequestParam("userId") Long userId);

    @PostMapping("/wishlist-item/delete/{id}")
    public void deleteWishlistItem(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @PostMapping("/review/add")
    public ReviewDTO addReview(@RequestBody ReviewAddDTO reviewAddDTO);

    @GetMapping("/review/{id}")
    public ReviewDTO getReviewById(@PathVariable("id") Long id);

    @GetMapping("/review/user/{id}")
    public List<ReviewDTO> getReviewsByUserId(@PathVariable("id") Long id);

    @GetMapping("/review/product/{id}")
    public List<ReviewDTO> getReviewsByProductId(@PathVariable("id") Long id);

    @PostMapping("/review/delete/{id}")
    public void deleteReview(@PathVariable("id") Long id, @RequestParam("userId") Long userId);
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
    public List<OrderHistoryDto> getOrdersHistory(@PathVariable String userId);
}
