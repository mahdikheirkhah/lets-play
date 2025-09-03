package com.gritlab.lets_play.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products") // Corrected from 'collation' to 'collection'
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String description;

    @Positive(message = "Price must be a positive value")
    private Double price;

    @NotBlank // A product must be linked to a user
    @Field("userId")
    public String userId;
}