package com.gritlab.lets_play.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Email
    private String email;

    public static UserUpdateDto fromEntity(User user) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setPassword(user.getPassword());
        userUpdateDto.setEmail(user.getEmail());
        return userUpdateDto;
    }
    public User toEntity() {
        User user = new User();
        user.setName(this.getName());
        user.setEmail(this.getEmail());
        user.setPassword(this.getPassword());
        user.setRole(Role.USER);
        return user;
    }
}
