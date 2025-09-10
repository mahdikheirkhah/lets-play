package com.gritlab.lets_play.service;

import com.gritlab.lets_play.exception.ResourceNotFoundException;
import com.gritlab.lets_play.exception.BadRequestException;
import com.gritlab.lets_play.model.Role;
import com.gritlab.lets_play.model.User;
import com.gritlab.lets_play.model.UserUpdateByAdminDto;
import com.gritlab.lets_play.model.UserUpdateDto;
import com.gritlab.lets_play.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


    public User authUser(UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database"));
    }

//    public boolean adminCheck(UserDetails currentUser){
//        return currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.name()));
//    }

    public boolean isEmailAvailable(String newEmail, String currentUserId) {
        Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);

        if (userWithNewEmail.isEmpty()) {
            return true;
        }

        return userWithNewEmail.get().getId().equals(currentUserId);
    }
    public User updateUser(UserUpdateDto userUpdateDto, User currentUser) {
        if (userUpdateDto.getName() != null && !userUpdateDto.getName().isBlank()) {
            currentUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(userUpdateDto.getPassword());
            currentUser.setPassword(hashedPassword);
        }

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isBlank()) {
            String newEmail = userUpdateDto.getEmail();

            // Check if the new email is already taken by ANOTHER user.
            Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);

            if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(currentUser.getId())) {
                throw new BadRequestException("The email '" + newEmail + "' is already taken.");
            }
            currentUser.setEmail(newEmail);
        }

        return userRepository.save(currentUser);
    }

    public User updateUserByAdmin(String ID, UserUpdateByAdminDto updateDto) {
        User userToUpdate = userRepository.findById(ID)
                    .orElseThrow(() -> new BadRequestException("No user found with ID: " + ID ));

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            userToUpdate.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            userToUpdate.setEmail(updateDto.getEmail());
        }
        if (updateDto.getRole() != null) {
            userToUpdate.setRole(updateDto.getRole());
        }

        return userRepository.save(userToUpdate);
    }
}