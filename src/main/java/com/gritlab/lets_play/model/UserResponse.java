package com.gritlab.lets_play.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserResponse {
    @NotBlank
    private String name;

    @NotBlank
    private Role role;

    @NotBlank
    @Email
    private String email;

    public static UserResponse fromEntity(User user) {
        UserResponse dto = new UserResponse();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

}
