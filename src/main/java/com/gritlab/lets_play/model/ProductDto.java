package com.gritlab.lets_play.model;

import lombok.Data;

@Data
public class ProductDto {
    private String name;
    private String description;
    private Double price;
    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
