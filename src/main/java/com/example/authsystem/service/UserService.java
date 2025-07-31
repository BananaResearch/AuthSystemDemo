package com.example.authsystem.service;

import com.example.authsystem.dto.JwtResponse;
import com.example.authsystem.dto.LoginRequest;
import com.example.authsystem.dto.RegisterRequest;
import com.example.authsystem.entity.User;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
    JwtResponse loginUser(LoginRequest loginRequest, String deviceId, String location);
    String refreshToken(String refreshToken);
    String forgotPassword(String email);
    void resetPassword(String email, String code, String newPassword);
}