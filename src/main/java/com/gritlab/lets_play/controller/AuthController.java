package com.gritlab.lets_play.controller;

import com.gritlab.lets_play.model.*;
import com.gritlab.lets_play.service.JwtService;
import com.gritlab.lets_play.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import java.util.Collections;

// In a new controller, e.g., AuthController.java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);

        // --- Create the Cookie ---
        Cookie cookie = new Cookie("jwt-token", jwtToken);
        cookie.setHttpOnly(true); // Makes it inaccessible to JavaScript
        cookie.setSecure(true); // Sent only over HTTPS (for production)
        cookie.setPath("/"); // Accessible from all paths
        cookie.setMaxAge(24 * 60 * 60); // 24 hours, same as token expiration
        // Consider setting SameSite attribute for CSRF protection
        //cookie.setSameSite("Strict");

        response.addCookie(cookie); // Add the cookie to the HTTP response

        // The response body now only needs the user data
        return ResponseEntity.ok("user with email: " + user.getEmail() + " successfully logged in");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto registerRequest) {

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setName(registerRequest.getName());
        newUser.setRole(Role.USER);
        User savedUser = userService.registerUser(newUser);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(savedUser.getRole().name()))
        );
        final String jwt = jwtService.generateToken(userDetails);

        // Return the token in the response
        return ResponseEntity.ok(jwt);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Create a cookie that expires immediately
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // This tells the browser to delete the cookie

        response.addCookie(cookie);
        return ResponseEntity.ok("Logout successful");
    }
}