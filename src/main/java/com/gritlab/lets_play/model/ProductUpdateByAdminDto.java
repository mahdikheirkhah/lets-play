package com.gritlab.lets_play.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductUpdateByAdminDto {
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;
    private String description;
    @PositiveOrZero(message = "Price must be a positive or zero value")
    private Double price;
    public String userId;
}
