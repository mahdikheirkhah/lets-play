package com.gritlab.lets_play.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.ProductRepository;
import com.gritlab.lets_play.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser; // A variable to hold our created user

    // This method runs BEFORE EACH test in this class
    @BeforeEach
    void setUp() {
        // Clean the database to ensure a fresh start
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Arrange: Create and save a user that our tests can use
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setName("Test User");
        user.setPassword(passwordEncoder.encode("password"));
        testUser = userRepository.save(user); // Save the user and store it
    }

    @Test
    void getAllProducts_ReturnsListOfProducts() throws Exception {
        // ... your existing test ...
    }

    @Test
    // Simulate being logged in as the user we created in setUp()
    @WithMockUser(username = "testuser@example.com")
    void addProduct_AuthenticatedUser_CreatesProduct() throws Exception {
        // Arrange: The 'testUser' already exists from the setUp() method.
        String productJson = "{\"name\":\"New Gadget\",\"description\":\"A cool gadget.\",\"price\":99.99}";

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Gadget"));
    }
}