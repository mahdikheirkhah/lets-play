package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.Product;
import com.gritlab.lets_play.model.ProductDto;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.ProductService;
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
    private final UserRepository userRepository; // Add this

    @Autowired
    public ProductController(ProductService productService, UserRepository userRepository) { // Add to constructor
        this.productService = productService;
        this.userRepository = userRepository; // Add this
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

        System.out.println(userDetails.getAuthorities());
        // 1. Get the email of the logged-in user from the UserDetails object.
        String userEmail = userDetails.getUsername();

        // 2. Find the full User entity from the database to get their actual ID.
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        // 3. Convert the request DTO to a Product entity.
            Product product = ProductDto.toEntity(productRequest);
        // 4. Call the service with the SECURE, authenticated owner's ID.
        Product savedProduct = productService.registerProduct(product, owner.getId());

        // 5. Convert the result to a DTO for the response.
        ProductDto responseDto = ProductDto.fromEntity(savedProduct);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
