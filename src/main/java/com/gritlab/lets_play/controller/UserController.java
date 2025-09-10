package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.*;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.ProductService;
import com.gritlab.lets_play.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user") // Using plural "users" is more RESTful
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList()); // .toList() can be immutable, collect is safer

        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getSpecificUsersWithID(@Valid @PathVariable String id) {
        User user = userService.getUserById(id);
        UserResponse userResponses = UserResponse.fromEntity(user);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getSpecificUsersWithEmail(@Valid @PathVariable String email) {
        User user = userService.getUserByEmail(email);
        UserResponse userResponses = UserResponse.fromEntity(user);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.authUser(userDetails);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.authUser(userDetails);
        user = userService.updateUser(userUpdateDto, user);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserByAdmin(
            @PathVariable String id, // Get the ID from the URL path
            @Valid @RequestBody UserUpdateByAdminDto userUpdateByAdminDto) {

        // The service method now takes the ID from the path
        User updatedUser = userService.updateUserByAdmin(id, userUpdateByAdminDto);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

}