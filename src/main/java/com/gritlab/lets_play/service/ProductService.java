package com.gritlab.lets_play.service;

import com.gritlab.lets_play.exception.BadRequestException;
import com.gritlab.lets_play.model.*;
import com.gritlab.lets_play.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.gritlab.lets_play.repository.ProductRepository;

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
        product.setUserId(ownerId);
        return productRepository.save(product);
    }
    public Product updateProduct(String productID,ProductUpdateDto productUpdateDto, User currentUser) {
        Product product = productRepository.findById(productID).orElseThrow(() ->new BadRequestException("product with this ID " + productID + "is not in the database"));
        if (!product.getUserId().equals(currentUser.getId())){
            throw new AccessDeniedException("you can not change this product");
        }

        if (productUpdateDto.getName() != null && !productUpdateDto.getName().isBlank()) {
            product.setName(productUpdateDto.getName());
        }
        if(productUpdateDto.getPrice() != null){
            product.setPrice(productUpdateDto.getPrice());
        }
        if(productUpdateDto.getDescription() != null && !productUpdateDto.getDescription().isBlank()){
            product.setDescription(productUpdateDto.getDescription());
        }

        return productRepository.save(product);
    }

    public Product updateProductByAdmin(String productID, ProductUpdateByAdminDto productUpdateDto) {
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new BadRequestException("No product found with ID: " + productID ));

        if (productUpdateDto.getName() != null && !productUpdateDto.getName().isBlank()) {
            product.setName(productUpdateDto.getName());
        }
        if(productUpdateDto.getPrice() != null){
            product.setPrice(productUpdateDto.getPrice());
        }
        if(productUpdateDto.getDescription() != null && !productUpdateDto.getDescription().isBlank()){
            product.setDescription(productUpdateDto.getDescription());
        }
        if(productUpdateDto.getUserId() != null && !productUpdateDto.getUserId().isBlank()){
            userRepository.findById(productUpdateDto.getUserId())
                    .orElseThrow(()-> new BadRequestException("No user with the provided ID found:" + productUpdateDto.getUserId()));
            product.setUserId(productUpdateDto.getUserId());
        }

        return productRepository.save(product);
    }
}
