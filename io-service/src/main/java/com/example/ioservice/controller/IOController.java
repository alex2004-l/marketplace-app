package com.example.ioservice.controller;

import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.model.ProductModel;
import com.example.ioservice.repository.ProductRepository;
import com.example.ioservice.service.IOService;
import lombok.RequiredArgsConstructor;
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
}
