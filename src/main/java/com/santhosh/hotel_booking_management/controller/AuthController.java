package com.santhosh.hotel_booking_management.controller;

import com.santhosh.hotel_booking_management.dto.request.LoginRequestDTO;
import com.santhosh.hotel_booking_management.dto.request.RegisterRequestDTO;
import com.santhosh.hotel_booking_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
      private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO
            ){
        authService.registerUser(registerRequestDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
            ){
         String token = authService.loginUser(loginRequestDTO);
         return ResponseEntity.ok(token);
    }
}
