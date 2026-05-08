package com.example.ioservice.controller;

import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.AddProductToCartDto;
import com.example.ioservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.ioservice.dto.ReviewDTOs.ReviewDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemDTO;
import com.example.ioservice.dto.*;
import com.example.ioservice.model.ProductModel;
import com.example.ioservice.repository.ProductRepository;
import com.example.ioservice.service.IOService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/io")
@RequiredArgsConstructor
public class IOController {
    private final IOService ioService;

    @PostMapping("/add_product")
    public ProductDto addProduct(@RequestBody ProductDto productDto) {
        return ioService.addProduct(productDto);
    }

    @GetMapping("/see_all_products")
    public List<ProductDto> seeAllProducts() {
        return ioService.seeAllProducts();
    }

    @GetMapping("/get/{id}")
    public ProductDto getProductById(@PathVariable("id") Long id) {
        return ioService.getProductById(id);
    }

    @GetMapping("/search")
    public List<ProductDto> searchByName(@RequestParam("name") String name) {
        return ioService.searchByName(name);
    }

    @PostMapping("/change-address")
    public String changeAddress() {
        return "TODO";
    }

    @PostMapping("/wishlist/add")
    public WishlistDTO addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO) {
        return ioService.addWishlist(wishlistAddDTO);
    }

    @GetMapping("/wishlist/{id}")
    public WishlistDTO getWishlistById(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return ioService.getWishlist(id, userId);
    }

    @PostMapping("/wishlist/delete/{id}")
    public void deleteWishlist(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        ioService.deleteWishlist(id, userId);
    }

    @PostMapping("/wishlist-item/add")
    public WishlistItemDTO addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO) {
        return ioService.addWishlistItem(wishlistItemAddDTO);
    }

    @GetMapping("/wishlist-item/{id}")
    public WishlistItemDTO getWishlistItemById(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return ioService.getWishlistItem(id, userId);
    }

    @GetMapping("/wishlist/name/{name}")
    public List<WishlistItemDTO> getWishlistItems(@PathVariable("name") String name, @RequestParam("userId") Long userId) {
        return ioService.getWishlistItems(name, userId);
    }

    @PostMapping("/wishlist-item/delete/{id}")
    public void deleteWishlistItem(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        ioService.deleteWishlistItem(id, userId);
    }

    @PostMapping("/review/add")
    public ReviewDTO addReview(@RequestBody ReviewAddDTO reviewAddDTO) {
        return ioService.addReview(reviewAddDTO);
    }

    @GetMapping("/review/{id}")
    public ReviewDTO getReviewById(@PathVariable("id") Long id) {
        return ioService.getReviewById(id);
    }

    @GetMapping("/review/user/{id}")
    public List<ReviewDTO> getReviewsByUserId(@PathVariable("id") Long id) {
        return ioService.getReviewsByUser(id);
    }

    @GetMapping("/review/product/{id}")
    public List<ReviewDTO> getReviewsByProductId(@PathVariable("id") Long id) {
        return ioService.getReviewsByProductId(id);
    }

    @PostMapping("/review/delete/{id}")
    public void deleteReview(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        ioService.deleteReview(id, userId);
    }

    @PostMapping("/add_product_to_cart")
    public String addProductToCart(@RequestBody AddProductToCartDto addProductToCartDto) {
        return ioService.addProductToCart(addProductToCartDto);
    }

    @PutMapping("/remove_one_product_from_cart")
    public String removeOneProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto) {
        return ioService.removeOneProduct(removeProductFromCartDto);
    }

    @PostMapping("/remove_product_from_cart")
    public String removeProduct(@RequestBody RemoveProductFromCartDto removeProductFromCartDto) {
        return ioService.removeProduct(removeProductFromCartDto);
    }

    @GetMapping("/get_cart_total/{userId}")
    public Float getCartTotal(@PathVariable String userId) {
        return ioService.getCartTotal(Long.valueOf(userId));
    }

    @PostMapping("/make_order")
    public String makeOrder(@RequestBody OrderDto orderDto) {
        return ioService.makeOrder(orderDto.userId());
    }

    @GetMapping("/get_orders_history/{userId}")
    public List<OrderHistoryDto> getOrdersHistory(@PathVariable String userId) {
        return ioService.getOrdersHistory(Long.valueOf(userId));
    }
}
