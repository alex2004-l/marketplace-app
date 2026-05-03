package com.example.marketservice.feignclient;

import com.example.marketservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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
    List<ProductDto> searchByName(@RequestParam("name") String name);

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
