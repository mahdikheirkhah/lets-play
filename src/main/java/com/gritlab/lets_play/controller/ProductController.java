package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.Product;
import com.gritlab.lets_play.model.ProductDto;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.ProductService;
import com.gritlab.lets_play.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService; // Add this

    @Autowired
    public ProductController(ProductService productService, UserService userService) { // Add to constructor
        this.productService = productService;
        this.userService = userService; // Add this
    }
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        // Hardcoded list for testing
        List<Product> products = productService.getProducts();
        List<ProductDto> productDos = products.stream()
                .map(ProductDto::fromEntity)
                .toList();

        return ResponseEntity.ok(productDos);
    }
    // Inside ProductController.java
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @Valid @RequestBody ProductDto productRequest,
            @AuthenticationPrincipal UserDetails userDetails) { // <-- Use @AuthenticationPrincipal

        User owner = userService.authUser(userDetails);
        Product product = ProductDto.toEntity(productRequest);
        Product savedProduct = productService.registerProduct(product, owner.getId());

        ProductDto responseDto = ProductDto.fromEntity(savedProduct);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
