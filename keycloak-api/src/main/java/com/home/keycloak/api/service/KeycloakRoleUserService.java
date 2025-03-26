package com.home.keycloak.api.service;

import com.home.keycloak.api.model.dto.UserRegistrationRecord;

public interface KeycloakRoleUserService {

    UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord);
    void deleteUser(String userId);

}
