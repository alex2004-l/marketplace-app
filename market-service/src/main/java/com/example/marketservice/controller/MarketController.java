package com.example.marketservice.controller;

import com.example.marketservice.dto.ProductDto;
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
}
