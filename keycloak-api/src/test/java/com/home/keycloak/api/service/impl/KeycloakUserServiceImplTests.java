package com.home.keycloak.api.service.impl;

import com.home.keycloak.api.exception.KeycloakUserCreationException;
import com.home.keycloak.api.exception.KeycloakUserNotFoundException;
import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakUserServiceImplTests {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private Response response;

    @InjectMocks
    private KeycloakUserServiceImpl keycloakUserService;

    private final String realmName = "test-realm";
    private final String userId = "12345";
    private final String username = "testuser";
    private final String email = "test@example.com";
    private final String firstName = "Test";
    private final String lastName = "User";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        keycloak = mock(Keycloak.class);
        realmResource = mock(RealmResource.class);
        usersResource = mock(UsersResource.class);

        keycloakUserService = new KeycloakUserServiceImpl(realmName, keycloak);
    }

    @Test
    void createUser_Success() {
        // Arrange
        UserRegistrationRecord registration = new UserRegistrationRecord(
                username, password, firstName, lastName, email);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201); // HTTP_CREATED

        // Act
        UserRegistrationRecord result = keycloakUserService.createUser(registration);

        // Assert
        assertEquals(registration, result);
        verify(usersResource).create(any(UserRepresentation.class));
    }

    @Test
    void createUser_Failure() {
        // Arrange
        UserRegistrationRecord registration = new UserRegistrationRecord(
                username, password, firstName, lastName, email);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400); // Bad request
        when(response.getStatusInfo()).thenReturn(Response.Status.BAD_REQUEST);

        // Act & Assert
        assertThrows(KeycloakUserCreationException.class, () -> {
            keycloakUserService.createUser(registration);
        });
    }

    @Test
    void createUser_InvalidInput() {
        // Arrange
        UserRegistrationRecord nullRegistration = new UserRegistrationRecord(
                null, null, null, null, null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            keycloakUserService.createUser(nullRegistration);
        });
    }

    @Test
    void getUserById_Success() {
        // Arrange
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId(userId);
        userRep.setUsername(username);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRep);

        // Act
        UserRepresentation result = keycloakUserService.getUserById(userId);

        // Assert
        assertEquals(userId, result.getId());
        assertEquals(username, result.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenThrow(new NotFoundException());

        // Act & Assert
        assertThrows(KeycloakUserNotFoundException.class, () -> {
            keycloakUserService.getUserById(userId);
        });
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        UserResource userResource = mock(UserResource.class);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);

        // Act
        keycloakUserService.deleteUser(userId);

        // Assert
        verify(userResource).remove();
    }

    @Test
    void deleteUser_NotFound() {
        // Arrange
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        doThrow(new NotFoundException()).when(usersResource).delete(userId);

        // Act & Assert
        assertThrows(KeycloakUserNotFoundException.class, () -> {
            keycloakUserService.deleteUser(userId);
        });
    }

    @Test
    void getUserResources_Success() {
        // Arrange
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId(userId);

        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRep);

        // Act
        UserResource result = keycloakUserService.getUserResources(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userResource, result);
    }

    @Test
    void getUserResources_NotFound() {
        // Arrange
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenThrow(new NotFoundException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            keycloakUserService.getUserResources(userId);
        });
    }

    @Test
    void createUserRepresentation_ValidInput() {
        // Arrange
        UserRegistrationRecord registration = new UserRegistrationRecord(
                username, password, firstName, lastName, email);

        // Act
        UserRepresentation result = keycloakUserService.createUserRepresentation(registration);

        // Assert
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertTrue(result.isEnabled());
        assertTrue(result.isEmailVerified());
        assertNotNull(result.getCredentials());
    }

    @Test
    void createCredentials_ValidInput() {
        // Act
        List<CredentialRepresentation> result = keycloakUserService.createCredentials(password);

        // Assert
        assertEquals(1, result.size());
        assertEquals(password, result.get(0).getValue());
        assertFalse(result.get(0).isTemporary());
    }
}