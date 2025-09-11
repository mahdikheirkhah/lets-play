package com.gritlab.lets_play.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // We are using email as the unique identifier (username)
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // For this simple case, we'll say accounts never expire
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Accounts are never locked
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Credentials (password) never expire
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Accounts are always enabled
        return true;
    }

}