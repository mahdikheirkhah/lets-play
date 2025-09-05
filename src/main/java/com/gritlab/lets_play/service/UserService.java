package com.gritlab.lets_play.service;

import com.gritlab.lets_play.model.Role;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Getter
    @Autowired
    private UserRepository userRepository;
    @Getter
    @Autowired
    private PasswordEncoder passwordEncoder;

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
}