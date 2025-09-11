package com.gritlab.lets_play.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDto {
    private String id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    private String description;
    @Positive(message = "Price must be a positive value")
    private Double price;
    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setId(product.getId());
        return dto;
    }
    public static Product toEntity(ProductDto productDto){
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        return  product;
    }
}
