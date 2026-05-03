package com.example.marketservice.controller;

import com.example.marketservice.dto.*;
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

    @GetMapping("/test_market")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> startCheck() {
        return ResponseEntity.ok("Market service working");
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
