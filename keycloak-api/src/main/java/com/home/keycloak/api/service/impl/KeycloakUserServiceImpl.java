package com.home.keycloak.api.service.impl;

import com.home.keycloak.api.exception.KeycloakUserCreationException;
import com.home.keycloak.api.exception.KeycloakUserNotFoundException;
import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakUserService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private static final int HTTP_CREATED = 201;
    private final String realmName;
    private final Keycloak keycloak;

    public KeycloakUserServiceImpl(@Value("${keycloak.realm}") String realmName, Keycloak keycloak) {
        this.realmName = realmName;
        this.keycloak = keycloak;
    }

    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {

        validateUserRegistration(userRegistrationRecord);
        try {
            UserRepresentation user = createUserRepresentation(userRegistrationRecord);
            Response response = getUsersResource().create(user);

            if (response.getStatus() == HTTP_CREATED) {
                log.info("User created successfully: {}", userRegistrationRecord.username());
                return userRegistrationRecord;
            }

            handleFailedUserCreation(response, userRegistrationRecord.username());
        } catch (Exception e) {
            log.error("Failed to create user: {}", userRegistrationRecord.username(), e);
            throw new KeycloakUserCreationException("Failed to create user: " + e.getMessage(), e);
        }

        return userRegistrationRecord;
    }


    @Override
    public UserRepresentation getUserById(String userId) {
        try {
            UserResource userResource = getUsersResource().get(userId);
            return userResource.toRepresentation();
        } catch (NotFoundException e) {
            log.warn("User not found with ID: {}", userId);
            throw new KeycloakUserNotFoundException("User not found with ID: " + userId);
        } catch (Exception e) {
            log.error("Failed to fetch user with ID: {}", userId, e);
            throw new KeycloakUserNotFoundException("Failed to fetch user: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteUser(String userId) {
        try {
            getUsersResource().delete(userId);
            log.info("Successfully deleted user with ID: {}", userId);
        } catch (NotFoundException e) {
            log.warn("Attempted to delete non-existent user with ID: {}", userId);
            throw new KeycloakUserNotFoundException("User not found with ID: " + userId);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", userId, e);
            throw new KeycloakUserCreationException("Failed to delete user: " + e.getMessage(), e);
        }
    }


    private UsersResource getUsersResource() {
        RealmResource realm = keycloak.realm(realmName);
        return realm.users();
    }

    private void validateUserRegistration(UserRegistrationRecord registration) {
        if (registration == null || registration.username() == null || registration.password() == null) {
            throw new IllegalArgumentException("Invalid user registration data");
        }
    }

    private UserRepresentation createUserRepresentation(UserRegistrationRecord registration) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(registration.username());
        user.setEmail(registration.email());
        user.setFirstName(registration.firstName());
        user.setLastName(registration.lastName());
        user.setEmailVerified(true);
        user.setCredentials(createCredentials(registration.password()));
        return user;
    }

    private List<CredentialRepresentation> createCredentials(String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(OAuth2Constants.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);
        return Collections.singletonList(credentials);
    }


    private void handleFailedUserCreation(Response response, String username) {
        String errorMessage = String.format(
                "Failed to create user %s. Status: %d, Reason: %s",
                username,
                response.getStatus(),
                response.getStatusInfo().getReasonPhrase()
        );

        log.error(errorMessage);
        throw new KeycloakUserCreationException(errorMessage);
    }

    @Override
    public UserResource getUserResources(String userId){
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }


}
