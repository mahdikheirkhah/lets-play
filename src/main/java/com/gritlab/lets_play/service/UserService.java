package com.gritlab.lets_play.service;

import com.gritlab.lets_play.model.Role;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Dependencies are injected through the constructor
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // 1. Check if a user with this email already exists
        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new IllegalStateException("Email already in use: " + user.getEmail());
        });

        // 2. Hash the password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // 3. Set a default role if one isn't provided (the safe way)
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // 4. Save the new user to the database
        return userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find your custom User entity from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert your User entity into a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}