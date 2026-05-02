package com.example.ioservice.controller;

import com.example.ioservice.dto.AddProductToCartDto;
import com.example.ioservice.dto.ProductDto;
import com.example.ioservice.dto.RemoveProductFromCartDto;
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
}
