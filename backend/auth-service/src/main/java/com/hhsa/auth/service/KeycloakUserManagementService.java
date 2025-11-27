package com.hhsa.auth.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Keycloak users programmatically.
 * Provides methods to create users, assign roles, and manage credentials.
 */
@Service
public class KeycloakUserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserManagementService.class);

    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserManagementService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    /**
     * Create a new user in Keycloak
     */
    public String createUser(String username, String email, String firstName, String lastName, 
                            String password, List<String> roles) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Check if user already exists
            List<UserRepresentation> existingUsers = usersResource.search(username);
            if (!existingUsers.isEmpty()) {
                logger.warn("User {} already exists", username);
                return existingUsers.get(0).getId();
            }

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Create user
            String userId = null;
            try (var response = usersResource.create(user)) {
                if (response.getStatus() == 201) {
                    // Extract user ID from location header
                    String location = response.getLocation().getPath();
                    userId = location.substring(location.lastIndexOf('/') + 1);
                    logger.info("User {} created with ID: {}", username, userId);
                } else {
                    logger.error("Failed to create user {}. Status: {}", username, response.getStatus());
                    throw new RuntimeException("Failed to create user: " + response.getStatus());
                }
            }

            if (userId == null) {
                throw new RuntimeException("Failed to create user: userId is null");
            }

            // Set password
            setUserPassword(userId, password);

            // Assign roles
            if (roles != null && !roles.isEmpty()) {
                assignRolesToUser(userId, roles);
            }

            return userId;
        } catch (Exception e) {
            logger.error("Error creating user {}", username, e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    /**
     * Set password for a user
     */
    public void setUserPassword(String userId, String password) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false); // Password is permanent, not temporary

            userResource.resetPassword(credential);
            logger.info("Password set for user ID: {}", userId);
        } catch (Exception e) {
            logger.error("Error setting password for user {}", userId, e);
            throw new RuntimeException("Failed to set password: " + e.getMessage(), e);
        }
    }

    /**
     * Assign roles to a user
     */
    public void assignRolesToUser(String userId, List<String> roleNames) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            UserResource userResource = realmResource.users().get(userId);
            RolesResource rolesResource = realmResource.roles();

            // Get realm-level roles
            List<RoleRepresentation> rolesToAssign = new ArrayList<>();
            for (String roleName : roleNames) {
                try {
                    RoleResource roleResource = rolesResource.get(roleName);
                    RoleRepresentation role = roleResource.toRepresentation();
                    rolesToAssign.add(role);
                } catch (Exception e) {
                    logger.warn("Role {} not found, skipping", roleName);
                }
            }

            if (!rolesToAssign.isEmpty()) {
                userResource.roles().realmLevel().add(rolesToAssign);
                logger.info("Assigned roles {} to user {}", roleNames, userId);
            }
        } catch (Exception e) {
            logger.error("Error assigning roles to user {}", userId, e);
            throw new RuntimeException("Failed to assign roles: " + e.getMessage(), e);
        }
    }

    /**
     * Create a role if it doesn't exist
     */
    public void createRoleIfNotExists(String roleName) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            RolesResource rolesResource = realmResource.roles();

            // Check if role exists
            try {
                rolesResource.get(roleName).toRepresentation();
                logger.debug("Role {} already exists", roleName);
                return;
            } catch (Exception e) {
                // Role doesn't exist, create it
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription("Role: " + roleName);
            rolesResource.create(role);
            logger.info("Role {} created", roleName);
        } catch (Exception e) {
            logger.error("Error creating role {}", roleName, e);
            throw new RuntimeException("Failed to create role: " + e.getMessage(), e);
        }
    }

    /**
     * Create all required roles for the application
     */
    public void createRequiredRoles() {
        List<String> requiredRoles = List.of(
            "ACCO_STAFF",
            "ACCO_MANAGER",
            "ACCO_ADMIN_STAFF",
            "PROGRAM_MANAGER",
            "FINANCE_STAFF",
            "FINANCE_MANAGER",
            "PROVIDER_MANAGER",
            "PROVIDER_STAFF"
        );

        for (String role : requiredRoles) {
            createRoleIfNotExists(role);
        }
        logger.info("All required roles created");
    }

    /**
     * Create test users with appropriate roles
     */
    public void createTestUsers() {
        createRequiredRoles();

        // ACCO Staff
        createUser("acco_staff", "acco_staff@hhsa.test", "ACCO", "Staff", 
                  "password123", List.of("ACCO_STAFF"));

        // ACCO Manager
        createUser("acco_manager", "acco_manager@hhsa.test", "ACCO", "Manager", 
                  "password123", List.of("ACCO_MANAGER"));

        // ACCO Admin Staff
        createUser("acco_admin", "acco_admin@hhsa.test", "ACCO", "Admin", 
                  "password123", List.of("ACCO_ADMIN_STAFF"));

        // Finance Staff
        createUser("finance_staff", "finance_staff@hhsa.test", "Finance", "Staff", 
                  "password123", List.of("FINANCE_STAFF"));

        // Finance Manager
        createUser("finance_manager", "finance_manager@hhsa.test", "Finance", "Manager", 
                  "password123", List.of("FINANCE_MANAGER"));

        // Program Manager
        createUser("program_manager", "program_manager@hhsa.test", "Program", "Manager", 
                  "password123", List.of("PROGRAM_MANAGER"));

        logger.info("All test users created");
    }
}

