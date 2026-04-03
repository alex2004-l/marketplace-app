package com.example.marketservice.feignclient;

import com.example.marketservice.dto.ProductDto;
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
}
