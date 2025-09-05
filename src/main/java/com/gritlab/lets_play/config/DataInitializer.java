package com.gritlab.lets_play.config;

import com.gritlab.lets_play.model.Product;
import com.gritlab.lets_play.model.Role;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.ProductRepository;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.ProductService;
import com.gritlab.lets_play.service.UserService; // Import the service
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserService userService,
                                   ProductService productService) { // 1. Inject UserService
        return args -> {
            // Clear old data
            userService.getUserRepository().deleteAll();
            productService.getProductRepository().deleteAll();

            // --- Create Users using the UserService ---
            User user1 = new User();
            user1.setName("mahdi");
            user1.setEmail("alice@example.com");
            user1.setRole(Role.ADMIN);
            user1.setPassword("password"); // Set the plain-text password

            User user2 = new User();
            user2.setName("bob");
            user2.setEmail("bob@example.com");
            user2.setRole(Role.USER);
            user2.setPassword("password");

            // 2. Call the service to handle registration logic
            User savedUser1 = userService.registerUser(user1);
            User savedUser2 = userService.registerUser(user2);

            // --- Create Products Linked to Saved Users ---
            Product p1 = new Product();
            p1.setName("Laptop Pro");
            p1.setDescription("A powerful new laptop.");
            p1.setPrice(1200.0);

            Product p2 = new Product();
            p2.setName("Smartphone X");
            p2.setDescription("The latest smartphone model.");
            p2.setPrice(800.50);
            p2.setUserId(savedUser1.getId());

            productService.registerProduct(p1,savedUser1.getId());
            productService.registerProduct(p2,savedUser2.getId());

            System.out.println("✅ Dummy data inserted using UserService!");
        };
    }
}