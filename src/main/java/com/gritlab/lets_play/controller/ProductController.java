package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.Product;
import com.gritlab.lets_play.model.ProductDto;
import com.gritlab.lets_play.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        // Hardcoded list for testing
        List<Product> products = productService.getProducts();
        List<ProductDto> productDos = products.stream()
                .map(ProductDto::fromEntity)
                .toList();

        return ResponseEntity.ok(productDos);
    }
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productRequest){
        Product product = ProductDto.toEntity(productRequest);
        product = productService.registerProduct(product, "68bb10ea2bd45d65fe53f86b");
        ProductDto responseDto = ProductDto.fromEntity(product);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
