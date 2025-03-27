package com.home.keycloak.api.service.impl;

import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakRoleUserService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.logging.Logger;

@Slf4j
@Service
public class KeycloakRoleUserServiceImpl implements KeycloakRoleUserService {


    private final Keycloak keycloak;

    public KeycloakRoleUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {

        try {
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(userRegistrationRecord.password());
            credentials.setTemporary(false);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRegistrationRecord.username());
            user.setEmail(userRegistrationRecord.email());
            user.setFirstName(userRegistrationRecord.firstName());
            user.setLastName(userRegistrationRecord.lastName());
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setCredentials(Collections.singletonList(credentials));

            var response = keycloak.realm("grow-me").users().create(user);

            if (response.getStatus() == 201) {
                log.info("User created successfully: {}", userRegistrationRecord.username());
                return userRegistrationRecord;
            } else {
                log.error("Failed to create user. Status: {}, Error: {}",
                        response.getStatus(), response.getStatusInfo());
                throw new RuntimeException("Failed to create user. Keycloak response: " + response.getStatus());
            }
        } catch (Exception e) {
            log.error("Keycloak user creation failed", e);
            throw new RuntimeException("Keycloak error: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteUser(String userId) {

    }


}
