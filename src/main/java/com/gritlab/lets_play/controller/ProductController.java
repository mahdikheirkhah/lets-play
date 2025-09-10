package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.*;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.ProductService;
import com.gritlab.lets_play.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getProducts();
        List<ProductDto> productDos = products.stream()
                .map(ProductDto::fromEntity)
                .toList();

        return ResponseEntity.ok(productDos);
    }
    @GetMapping("/myProduct")
    public ResponseEntity<List<ProductDto>> getAllMyProducts(@AuthenticationPrincipal UserDetails userDetails) {
        User owner = userService.authUser(userDetails);
        List<Product> products = productService.getProducts(owner.getId());
        List<ProductDto> productDos = products.stream()
                .map(ProductDto::fromEntity)
                .toList();

        return ResponseEntity.ok(productDos);
    }
    @PostMapping("/create")
    public ResponseEntity<ProductDto> addProduct(
            @Valid @RequestBody ProductDto productRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        User owner = userService.authUser(userDetails);
        Product product = ProductDto.toEntity(productRequest);
        Product savedProduct = productService.registerProduct(product, owner.getId());

        ProductDto responseDto = ProductDto.fromEntity(savedProduct);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @PutMapping("/product/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id,@Valid @RequestBody ProductUpdateDto productUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.authUser(userDetails);
        Product product = productService.updateProduct(id, productUpdateDto, user);
        return ResponseEntity.ok(ProductDto.fromEntity(product));
    }
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> updateProductByAdmin(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateByAdminDto productUpdateByAdminDto) {

        Product product = productService.updateProductByAdmin(id, productUpdateByAdminDto);
        return ResponseEntity.ok(ProductDto.fromEntity(product));
    }
    // Endpoint for a user to delete their own product
    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.authUser(userDetails);
        productService.deleteProduct(id, currentUser);

        // Return 204 No Content on successful deletion
        return ResponseEntity.noContent().build();
    }

    // Endpoint for an admin to delete any product
    // DELETE /api/products/admin/{id}
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProductByAdmin(@PathVariable String id) {
        productService.deleteProductByAdmin(id);
        return ResponseEntity.noContent().build();
    }

}
