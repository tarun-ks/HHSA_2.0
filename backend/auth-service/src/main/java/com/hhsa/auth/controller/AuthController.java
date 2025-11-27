package com.hhsa.auth.controller;

import com.hhsa.auth.dto.LoginRequest;
import com.hhsa.auth.dto.LoginResponse;
import com.hhsa.auth.dto.RefreshTokenRequest;
import com.hhsa.auth.service.AuthService;
import com.hhsa.auth.service.AuthServiceFactory;
import com.hhsa.common.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller.
 * Provides login, logout, token refresh, and user info endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthServiceFactory authServiceFactory;

    public AuthController(AuthServiceFactory authServiceFactory) {
        this.authServiceFactory = authServiceFactory;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get access token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthService authService = authServiceFactory.getAuthService(request.getProvider());
        ApiResponse<LoginResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // For refresh, use the same provider that issued the token
        // In a real implementation, you might decode the token to determine provider
        AuthService authService = authServiceFactory.getDefaultAuthService();
        ApiResponse<LoginResponse> response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        AuthService authService = authServiceFactory.getDefaultAuthService();
        ApiResponse<Void> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get current user information from token")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        AuthService authService = authServiceFactory.getDefaultAuthService();
        ApiResponse<LoginResponse.UserInfo> response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }
}




