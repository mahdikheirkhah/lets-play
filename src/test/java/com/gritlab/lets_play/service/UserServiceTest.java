package com.gritlab.lets_play.service;

import com.gritlab.lets_play.model.Role;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
class UserServiceTest {

    @Mock // Creates a mock implementation for the UserRepository
    private UserRepository userRepository;

    @Mock // Creates a mock for the PasswordEncoder
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Creates an instance of UserService and injects the mocks into it
    private UserService userService;

    @Test
    void registerUser_Success() {
        // --- Arrange ---
        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");

        // Define the behavior of the mocks
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        User savedUser = userService.registerUser(newUser);

        // --- Assert ---
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole()); // Check for default role
        verify(userRepository, times(1)).save(any(User.class)); // Verify save was called once
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // --- Arrange ---
        User existingUser = new User();
        existingUser.setEmail("test@example.com");

        // Mock the repository to find an existing user
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // --- Act & Assert ---
        // Verify that the correct exception is thrown
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.registerUser(existingUser);
        });

        assertEquals("Email already in use: test@example.com", exception.getMessage());
        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }
}