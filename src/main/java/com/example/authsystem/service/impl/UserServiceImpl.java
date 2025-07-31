package com.example.authsystem.service.impl;

import com.example.authsystem.dto.JwtResponse;
import com.example.authsystem.dto.LoginRequest;
import com.example.authsystem.dto.RegisterRequest;
import com.example.authsystem.entity.User;
import com.example.authsystem.entity.VerificationCode;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.repository.VerificationCodeRepository;
import com.example.authsystem.service.UserService;
import com.example.authsystem.util.JwtUtil;
import com.example.authsystem.util.PasswordUtil;
import com.example.authsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public User registerUser(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordUtil.encodePassword(registerRequest.getPassword())
        );
        
        return userRepository.save(user);
    }
    
    @Override
    public JwtResponse loginUser(LoginRequest loginRequest, String deviceId, String location) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        // Check login attempts
        String loginAttemptsKey = "login_attempts:" + username;
        String lockKey = "login_lock:" + username;
        
        // Check if account is locked
        if (redisUtil.hasKey(lockKey)) {
            throw new RuntimeException("Account is locked due to multiple failed login attempts. Please try again later.");
        }
        
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        // Check if account is active
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("Account is not active!");
        }
        
        // Validate password
        if (!passwordUtil.matches(password, user.getPasswordHash())) {
            // Increment login attempts
            redisUtil.increment(loginAttemptsKey, 1);
            redisUtil.expire(loginAttemptsKey, 5, java.util.concurrent.TimeUnit.MINUTES);
            
            // Check if attempts exceed limit
            String attempts = redisUtil.get(loginAttemptsKey);
            if (attempts != null && Integer.parseInt(attempts) >= 3) {
                // Lock account for 15 minutes
                redisUtil.set(lockKey, "LOCKED", 15, java.util.concurrent.TimeUnit.MINUTES);
                throw new RuntimeException("Too many failed login attempts. Account locked for 15 minutes.");
            }
            
            throw new RuntimeException("Invalid password!");
        }
        
        // Reset login attempts
        redisUtil.delete(loginAttemptsKey);
        
        // Update last login time
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername(), deviceId, location);
        String refreshToken = UUID.randomUUID().toString();
        
        // Store refresh token in Redis
        String refreshTokenKey = "refresh_token:" + user.getId();
        redisUtil.set(refreshTokenKey, refreshToken, 7, java.util.concurrent.TimeUnit.DAYS);
        
        return new JwtResponse(accessToken, refreshToken, user.getId(), user.getUsername());
    }
    
    @Override
    public String refreshToken(String refreshToken) {
        // In a real implementation, you would validate the refresh token
        // and generate a new access token. For simplicity, we'll just
        // generate a new token without validation.
        return jwtUtil.generateToken("user", "device", "location");
    }
    
    @Override
    public String forgotPassword(String email) {
        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Generate verification code
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // Save verification code
        VerificationCode verificationCode = new VerificationCode(
                email,
                code,
                "PASSWORD_RESET",
                LocalDateTime.now().plusMinutes(10)
        );
        verificationCodeRepository.save(verificationCode);
        
        // In a real implementation, you would send the code via email
        // For now, we'll just print it to the console
        System.out.println("Password reset code for " + email + ": " + code);

        return code;
    }
    
    @Override
    public void resetPassword(String email, String code, String newPassword) {
        // Find valid verification code
        LocalDateTime now = LocalDateTime.now();
        verificationCodeRepository.findByEmailAndCodeAndTypeAndStatusAndExpireTimeAfter(
                email, code, "PASSWORD_RESET", "ACTIVE", now)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification code"));
        
        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Update password
        user.setPasswordHash(passwordUtil.encodePassword(newPassword));
        userRepository.save(user);
        
        // Mark verification code as used
        List<VerificationCode> codes = verificationCodeRepository.findByEmailAndTypeAndStatusOrderByExpireTimeDesc(
                email, "PASSWORD_RESET", "ACTIVE");
        for (VerificationCode vc : codes) {
            vc.setStatus("USED");
            verificationCodeRepository.save(vc);
        }
    }
}