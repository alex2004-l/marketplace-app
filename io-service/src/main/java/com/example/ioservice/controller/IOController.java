package com.example.ioservice.controller;

import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.AddProductToCartDto;
import com.example.ioservice.dto.ReviewDTOs.ReviewAddDTO;
import com.example.ioservice.dto.ReviewDTOs.ReviewDTO;
import com.example.ioservice.dto.UserDTOs.AddUserDto;
import com.example.ioservice.dto.UserDTOs.UserDTO;
import com.example.ioservice.dto.UserDTOs.UserUpdateDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistAddDTO;
import com.example.ioservice.dto.WishlistDTOs.WishlistDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemAddDTO;
import com.example.ioservice.dto.WishlistItemDTOs.WishlistItemDTO;
import com.example.ioservice.dto.*;
import com.example.ioservice.model.UserModel;
import com.example.ioservice.service.IOService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/internal/io")
@RequiredArgsConstructor
public class IOController {
    private final IOService ioService;

    @PostMapping("/add_product")
    public ProductDto addProduct(@RequestBody ProductSecDto productDto) {
        return ioService.addProduct(productDto);
    }

    @GetMapping("/see_all_products")
    public List<ProductDto> seeAllProducts() {
        return ioService.seeAllProducts();
    }

    @GetMapping("/product/{id}")
    public ProductDto getProductById(@PathVariable("id") Long id) {
        return ioService.getProductById(id);
    }

    @PutMapping("/product/{id}")
    public ProductDto updateProduct(@PathVariable("id") Long id, @RequestBody ProductUpdateDTO productUpdateDTO, @RequestParam("keycloakId") String keycloakId) {
        return ioService.updateProduct(id, productUpdateDTO, keycloakId);
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        ioService.deleteProduct(id, keycloakId);
    }

    @GetMapping("/search")
    public List<ProductDto> searchByName(SearchProductDTO searchProductDTO) {
        return ioService.searchByName(searchProductDTO);
    }

    @PostMapping("/wishlist/add")
    public WishlistDTO addWishlist(@RequestBody WishlistAddDTO wishlistAddDTO, @RequestParam("keycloakId") String keycloakId) {
        return ioService.addWishlist(wishlistAddDTO, keycloakId);
    }

    @GetMapping("/wishlist/{id}")
    public WishlistDTO getWishlistById(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        return ioService.getWishlist(id, keycloakId);
    }

    @DeleteMapping("/wishlist/{id}")
    public void deleteWishlist(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        ioService.deleteWishlist(id, keycloakId);
    }

    @PostMapping("/wishlist-item/add")
    public WishlistItemDTO addWishlistItem(@RequestBody WishlistItemAddDTO wishlistItemAddDTO, @RequestParam("keycloakId") String keycloakId) {
        return ioService.addWishlistItem(wishlistItemAddDTO, keycloakId);
    }

    @GetMapping("/wishlist-item/{id}")
    public WishlistItemDTO getWishlistItemById(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        return ioService.getWishlistItem(id, keycloakId);
    }

    @GetMapping("/wishlist/name/{name}")
    public List<WishlistItemDTO> getWishlistItems(@PathVariable("name") String name, @RequestParam("keycloakId") String keycloakId) {
        return ioService.getWishlistItems(name, keycloakId);
    }

    @DeleteMapping("/wishlist-item/{id}")
    public void deleteWishlistItem(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        ioService.deleteWishlistItem(id, keycloakId);
    }

    @PostMapping("/review/add")
    public ReviewDTO addReview(@RequestBody ReviewAddDTO reviewAddDTO, @RequestParam("keycloakId") String keycloakId) {
        return ioService.addReview(reviewAddDTO, keycloakId);
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

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable("id") Long id, @RequestParam("keycloakId") String keycloakId) {
        ioService.deleteReview(id, keycloakId);
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
        return ioService.getCartTotal(userId);
    }

    @PostMapping("/make_order")
    public String makeOrder(@RequestBody OrderDto orderDto) {
        return ioService.makeOrder(orderDto.keycloakId());
    }

    @GetMapping("/get_orders_history/{userId}")
    public List<OrderHistoryDto> getOrdersHistory(@PathVariable String userId) {
        return ioService.getOrdersHistory(userId);
    }

    @GetMapping("/get_cart/{userId}")
    public CartDto getCartProducts(@PathVariable String userId) {
        return ioService.getCartProducts(userId);
    }

    @PostMapping("/user/update")
    public UserDTO updateUserData(@RequestBody UserUpdateDTO userDTO, @RequestParam("keycloakId") String keycloakId) {
        return ioService.updateUserData(userDTO, keycloakId);
    }

    @GetMapping("/user/check_if_exists/{id}")
    public Boolean checkIfUserExists(@PathVariable String id) {
        return ioService.checkIfUserExists(id);
    }

    @PostMapping("/user/add_user")
    public void addUser(@RequestBody AddUserDto dto) {
        ioService.addUser(dto);
    }

    @GetMapping("/user/find_user/{id}")
    public Optional<UserModel> findUserByKeycloakId(@PathVariable String id) {
        return ioService.findUserByKeycloakId(id);
    }

    @DeleteMapping("/user/delete_users")
    public void deleteUsersTable() {
        ioService.deleteUsersTable();
    }
}
