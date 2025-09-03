package com.gritlab.lets_play.repository;

import com.gritlab.lets_play.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // Find all products that belong to a specific user
    List<Product> findByUserId(String userId);
}