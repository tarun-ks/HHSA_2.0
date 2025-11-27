package com.hhsa.auth.service;

import com.hhsa.auth.dto.LoginRequest;
import com.hhsa.auth.dto.LoginResponse;
import com.hhsa.auth.dto.RefreshTokenRequest;
import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Keycloak authentication service implementation.
 * Primary authentication provider.
 */
@Service("keycloakAuthService")
public class KeycloakAuthService implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthService.class);

    private final Keycloak keycloakAdminClient;
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public KeycloakAuthService(
            Keycloak keycloakAdminClient,
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        try {
            logger.debug("Attempting Keycloak login for user: {}", request.getUsername());

            // Call Keycloak token endpoint directly using HTTP
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", request.getUsername());
            body.add("password", request.getPassword());
            
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            
            // Get access token
            Map<String, Object> tokenResponse;
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, 
                    HttpMethod.POST, 
                    requestEntity, 
                    Map.class
                );
                
                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    throw new BaseException("AUTH_ERROR", "Failed to authenticate with Keycloak");
                }
                
                tokenResponse = response.getBody();
            } catch (Exception e) {
                logger.error("Failed to get access token from Keycloak. Realm: {}, Client: {}", realm, clientId, e);
                throw new BaseException("AUTH_ERROR", "Authentication failed: " + e.getMessage() + ". Please verify realm and client configuration.");
            }

            // Extract user information from JWT token (avoid admin client version mismatch)
            String accessToken = (String) tokenResponse.get("access_token");
            Map<String, Object> tokenClaims = decodeJwtToken(accessToken);
            
            // Extract user info from token claims
            String userId = (String) tokenClaims.get("sub");
            String username = (String) tokenClaims.get("preferred_username");
            String email = (String) tokenClaims.get("email");
            String firstName = (String) tokenClaims.get("given_name");
            String lastName = (String) tokenClaims.get("family_name");
            
            // Extract roles from token
            @SuppressWarnings("unchecked")
            Map<String, Object> realmAccess = (Map<String, Object>) tokenClaims.get("realm_access");
            List<String> roles = List.of();
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                List<String> realmRoles = (List<String>) realmAccess.get("roles");
                if (realmRoles != null) {
                    roles = realmRoles;
                }
            }

            // Build user info
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                userId,
                username != null ? username : request.getUsername(),
                email,
                firstName,
                lastName,
                roles
            );

            // Build login response
            Object expiresInObj = tokenResponse.get("expires_in");
            Long expiresIn = expiresInObj instanceof Number ? ((Number) expiresInObj).longValue() : 3600L;
            
            LoginResponse response = new LoginResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                expiresIn,
                userInfo
            );

            logger.info("Keycloak login successful for user: {}", request.getUsername());
            return ApiResponse.success(response, "Login successful");

        } catch (Exception e) {
            logger.error("Keycloak login failed for user: {}", request.getUsername(), e);
            throw new BaseException("AUTH_ERROR", "Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest request) {
        try {
            logger.debug("Attempting token refresh");

            // Call Keycloak token endpoint directly using HTTP for refresh
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", request.getRefreshToken());
            
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            
            // Refresh the token
            Map<String, Object> tokenResponse;
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, 
                    HttpMethod.POST, 
                    requestEntity, 
                    Map.class
                );
                
                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    throw new BaseException("AUTH_ERROR", "Failed to refresh token with Keycloak");
                }
                
                tokenResponse = response.getBody();
            } catch (Exception e) {
                logger.error("Failed to refresh token from Keycloak", e);
                throw new BaseException("AUTH_ERROR", "Token refresh failed: " + e.getMessage());
            }

            // For refresh, we don't need to fetch user info again
            // User info can be extracted from token if needed
            Object expiresInObj = tokenResponse.get("expires_in");
            Long expiresIn = expiresInObj instanceof Number ? ((Number) expiresInObj).longValue() : 3600L;
            
            LoginResponse loginResponse = new LoginResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                expiresIn,
                null // User info can be extracted from token if needed
            );

            logger.info("Token refresh successful");
            return ApiResponse.success(loginResponse, "Token refreshed successfully");

        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new BaseException("AUTH_ERROR", "Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> logout(String token) {
        try {
            logger.debug("Attempting logout");

            // Keycloak logout - in a real implementation, you might want to call Keycloak's logout endpoint
            // For now, we'll just log it. Token invalidation is typically handled client-side
            // by removing the token from storage.

            logger.info("Logout successful");
            return ApiResponse.success(null, "Logout successful");

        } catch (Exception e) {
            logger.error("Logout failed", e);
            throw new BaseException("AUTH_ERROR", "Logout failed: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<LoginResponse.UserInfo> getCurrentUser(String token) {
        try {
            // Decode JWT token to extract user information
            Map<String, Object> tokenClaims = decodeJwtToken(token);
            
            String userId = (String) tokenClaims.get("sub");
            String username = (String) tokenClaims.get("preferred_username");
            String email = (String) tokenClaims.get("email");
            String firstName = (String) tokenClaims.get("given_name");
            String lastName = (String) tokenClaims.get("family_name");
            
            // Extract roles from token
            @SuppressWarnings("unchecked")
            Map<String, Object> realmAccess = (Map<String, Object>) tokenClaims.get("realm_access");
            List<String> roles = List.of();
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                List<String> realmRoles = (List<String>) realmAccess.get("roles");
                if (realmRoles != null) {
                    roles = realmRoles;
                }
            }
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                userId,
                username,
                email,
                firstName,
                lastName,
                roles
            );
            
            return ApiResponse.success(userInfo, "User information retrieved successfully");
        } catch (Exception e) {
            logger.error("Get current user failed", e);
            throw new BaseException("AUTH_ERROR", "Failed to get current user: " + e.getMessage());
        }
    }
    
    /**
     * Decode JWT token without verification (since we trust Keycloak)
     * In production, you should verify the token signature
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> decodeJwtToken(String token) {
        try {
            // JWT tokens have 3 parts separated by dots: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BaseException("AUTH_ERROR", "Invalid JWT token format");
            }
            
            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse JSON payload
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(payload, Map.class);
        } catch (Exception e) {
            logger.error("Failed to decode JWT token", e);
            throw new BaseException("AUTH_ERROR", "Failed to decode token: " + e.getMessage());
        }
    }
}


