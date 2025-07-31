package com.example.authsystem.controller;

import com.example.authsystem.dto.ForgotPasswordRequest;
import com.example.authsystem.dto.JwtResponse;
import com.example.authsystem.dto.LoginRequest;
import com.example.authsystem.dto.RegisterRequest;
import com.example.authsystem.dto.ResetPasswordRequest;
import com.example.authsystem.entity.User;
import com.example.authsystem.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Register request received: username={}, email={}", registerRequest.getUsername(), registerRequest.getEmail());
        try {
            User user = userService.registerUser(registerRequest);
            return ResponseEntity.ok().body("User registered successfully! User ID: " + user.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                      @RequestHeader(value = "Device-ID", required = false) String deviceId,
                                      @RequestHeader(value = "Location", required = false) String location) {
        try {
            deviceId = deviceId != null ? deviceId : "unknown";
            location = location != null ? location : "unknown";
            
            JwtResponse jwtResponse = userService.loginUser(loginRequest, deviceId, location);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            String newAccessToken = userService.refreshToken(refreshToken);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken, null, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String code = userService.forgotPassword(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok().body("Password reset code sent to your email. code: " + code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            userService.resetPassword(
                    resetPasswordRequest.getEmail(),
                    resetPasswordRequest.getCode(),
                    resetPasswordRequest.getNewPassword()
            );
            return ResponseEntity.ok().body("Password reset successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}