package com.gritlab.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gritlab.lets_play.repository.ProductRepository;
import com.gritlab.lets_play.model.Product;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> GetProducts(){
        return  productRepository.findAll();
    }
}
