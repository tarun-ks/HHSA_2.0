package com.hhsa.auth.controller;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.auth.service.KeycloakUserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Keycloak user management operations.
 * Provides endpoints to create users and roles programmatically.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User Management", description = "Keycloak user management endpoints (Admin only)")
public class UserManagementController {

    private final KeycloakUserManagementService userManagementService;

    public UserManagementController(KeycloakUserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create User", description = "Create a new user in Keycloak with roles")
    public ResponseEntity<ApiResponse<Map<String, String>>> createUser(
            @RequestBody CreateUserRequest request) {
        try {
            String userId = userManagementService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPassword(),
                request.getRoles()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("userId", userId), "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), 
                    new ApiResponse.ErrorDetails("USER_CREATE_ERROR", e.getMessage())));
        }
    }

    @PostMapping("/create-test-users")
    @Operation(summary = "Create Test Users", description = "Create all test users with required roles")
    public ResponseEntity<ApiResponse<Void>> createTestUsers() {
        try {
            userManagementService.createTestUsers();
            return ResponseEntity.ok(ApiResponse.success(null, "All test users created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage(), 
                    new ApiResponse.ErrorDetails("TEST_USERS_CREATE_ERROR", e.getMessage())));
        }
    }

    @PostMapping("/create-roles")
    @Operation(summary = "Create Required Roles", description = "Create all required application roles")
    public ResponseEntity<ApiResponse<Void>> createRequiredRoles() {
        try {
            userManagementService.createRequiredRoles();
            return ResponseEntity.ok(ApiResponse.success(null, "All required roles created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage(), 
                    new ApiResponse.ErrorDetails("ROLES_CREATE_ERROR", e.getMessage())));
        }
    }

    /**
     * Request DTO for creating a user
     */
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private List<String> roles;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
}

