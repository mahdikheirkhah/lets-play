package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.Product;
import com.gritlab.lets_play.model.ProductDto;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.model.UserResponse;
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
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository; // Add this

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) { // Add to constructor
        this.userService = userService;
        this.userRepository = userRepository; // Add this
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userDos = users.stream()
                .map(UserResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(userDos);
    }
    // Inside ProductController.java
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @Valid @RequestBody ProductDto productRequest,
            @AuthenticationPrincipal UserDetails userDetails) { // <-- Use @AuthenticationPrincipal

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
