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
        Cookie cookie = JwtService.createCookie(jwtToken, 24*60*60);
        response.addCookie(cookie);
        return ResponseEntity.ok("user with email: " + user.getEmail() + " successfully logged in");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto registerRequest,  HttpServletResponse response) {

        User newUser = UserDto.toEntity(registerRequest);
        User savedUser = userService.registerUser(newUser);
        final String jwtToken = jwtService.generateToken(savedUser);
        Cookie cookie = JwtService.createCookie(jwtToken, 24*60*60);
        response.addCookie(cookie);
        return ResponseEntity.ok("user with email: " + savedUser.getEmail() + " successfully registered and logged in");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = JwtService.createCookie(null, 0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout successful");
    }
}