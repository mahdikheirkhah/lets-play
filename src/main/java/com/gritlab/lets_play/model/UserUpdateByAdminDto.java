package com.gritlab.lets_play.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateByAdminDto {

    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private Role role;

    @Email
    private String email;

    public User toEntity() {
        User user = new User();
        user.setName(this.getName());
        user.setEmail(this.getEmail());
        user.setPassword(this.getPassword());
        user.setRole(this.getRole());
        return user;
    }
}
