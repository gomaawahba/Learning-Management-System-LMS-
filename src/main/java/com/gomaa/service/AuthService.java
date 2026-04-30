package com.gomaa.service;

import com.gomaa.dto.request.LoginRequest;
import com.gomaa.dto.request.RegisterRequest;
import com.gomaa.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String token);
    void logout(Long userId);
}