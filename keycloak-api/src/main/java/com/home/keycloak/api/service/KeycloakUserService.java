package com.home.keycloak.api.service;

import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakUserService {

    UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord);
    UserRepresentation getUserById(String userId);
    void deleteUser(String userId);

}
