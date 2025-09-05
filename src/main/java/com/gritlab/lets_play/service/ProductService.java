package com.gritlab.lets_play.service;

import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gritlab.lets_play.repository.ProductRepository;
import com.gritlab.lets_play.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Getter
    @Autowired
    ProductRepository productRepository;

    @Getter
    @Autowired
    UserRepository userRepository;

    public List<Product> getProducts(){
        return  productRepository.findAll();
    }
    public List<Product> getProducts(String userID){
        return productRepository.findByUserId(userID);
    }
    public Product registerProduct(Product product, String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new IllegalStateException("Authenticated user not found with id: " + ownerId));

        product.setUserId(owner.getId());
        return productRepository.save(product);
    }
}
