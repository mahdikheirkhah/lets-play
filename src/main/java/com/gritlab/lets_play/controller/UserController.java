package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.*;
import com.gritlab.lets_play.repository.UserRepository;
import com.gritlab.lets_play.service.JwtService;
import com.gritlab.lets_play.service.ProductService;
import com.gritlab.lets_play.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user") // Using plural "users" is more RESTful
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList()); // .toList() can be immutable, collect is safer

        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getSpecificUsersWithID(@Valid @PathVariable String id) {
        User user = userService.getUserById(id);
        UserResponse userResponses = UserResponse.fromEntity(user);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getSpecificUsersWithEmail(@Valid @PathVariable String email) {
        User user = userService.getUserByEmail(email);
        UserResponse userResponses = UserResponse.fromEntity(user);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.authUser(userDetails);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    @PutMapping("/me")
    public ResponseEntity<UserUpdateDto> updateCurrentUser(
            @Valid @RequestBody UserUpdateDto userUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) { // <-- Inject HttpServletResponse

        User currentUser = userService.authUser(userDetails);

        // 1. Call the updated service method
        UserService.UserUpdateResult result = userService.updateUser(userUpdateDto, currentUser);

        // 2. Check if a new token is needed
        if (result.tokenInvalidated()) {
            // 3. Generate a new token
            String newToken = jwtService.generateToken(result.updatedUser());

            // 4. Create and set the new cookie, overwriting the old one
            Cookie cookie = new Cookie("jwt-token", newToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(cookie);
        }

        // 5. Return only the updated user data in the response body
        return ResponseEntity.ok(UserUpdateDto.fromEntity(result.updatedUser()));
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
    /**
     * Endpoint for a user to delete their own account.
     */
    // DELETE /api/users/me
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUserAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.authUser(userDetails); // Assuming you have this helper
        userService.deleteCurrentUser(currentUser);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    /**
     * Endpoint for an admin to delete any user account.
     */
    // DELETE /api/users/admin/{id}
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String id) {
        userService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

}