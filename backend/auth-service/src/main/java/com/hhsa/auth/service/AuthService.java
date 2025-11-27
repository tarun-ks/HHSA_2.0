package com.hhsa.auth.service;

import com.hhsa.auth.dto.LoginRequest;
import com.hhsa.auth.dto.LoginResponse;
import com.hhsa.auth.dto.RefreshTokenRequest;
import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Authentication service interface.
 * Uses Keycloak for authentication.
 */
public interface AuthService {

    /**
     * Authenticate user and return tokens
     */
    ApiResponse<LoginResponse> login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest request);

    /**
     * Logout user (invalidate tokens)
     */
    ApiResponse<Void> logout(String token);

    /**
     * Get current user information from JWT token
     */
    ApiResponse<LoginResponse.UserInfo> getCurrentUser(String token);
}




