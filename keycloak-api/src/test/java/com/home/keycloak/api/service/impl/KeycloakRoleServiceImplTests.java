package com.home.keycloak.api.service.impl;


import com.home.keycloak.api.service.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KeycloakRoleServiceImplTests {
    private Keycloak keycloak;
    private RealmResource realmResource;
    private RolesResource rolesResource;
    private RoleResource roleResource;
    private RoleRepresentation roleRepresentation;
    private KeycloakUserService keycloakUserService;
    private UserResource userResource;

    private KeycloakRoleServiceImpl roleService;

    private final String realmName = "test-realm";
    private final String userId = "user-123";
    private final String roleName = "SELLER";

    @BeforeEach
    void setUp() {
        keycloak = mock(Keycloak.class);
        realmResource = mock(RealmResource.class);
        rolesResource = mock(RolesResource.class);
        roleResource = mock(RoleResource.class);
        roleRepresentation = mock(RoleRepresentation.class);
        keycloakUserService = mock(KeycloakUserService.class);
        userResource = mock(UserResource.class);

        // Set up Keycloak role service with mocks
        roleService = new KeycloakRoleServiceImpl(keycloak, realmName, keycloakUserService);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(roleName)).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
        when(keycloakUserService.getUserResources(userId)).thenReturn(userResource);
    }



    @Test
    void assignRole_roleNotFound_shouldThrowIllegalArgumentException() {
        // Arrange
        when(roleResource.toRepresentation()).thenReturn(null);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                roleService.assignRole(userId, roleName));
        assertEquals("Role SELLER does not exist!", exception.getMessage());
    }

    @Test
    void assignRole_runtimeException_shouldWrapInRuntimeException() {
        // Arrange
        when(keycloakUserService.getUserResources(userId)).thenThrow(new RuntimeException("User not found"));

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                roleService.assignRole(userId, roleName));
        assertTrue(exception.getMessage().contains("Failed to assign role"));
        assertTrue(exception.getCause() instanceof RuntimeException);
    }
}
