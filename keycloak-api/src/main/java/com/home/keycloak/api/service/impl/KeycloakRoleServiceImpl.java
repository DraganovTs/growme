package com.home.keycloak.api.service.impl;

import com.home.keycloak.api.service.KeycloakRoleService;
import com.home.keycloak.api.service.KeycloakUserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakRoleServiceImpl implements KeycloakRoleService {

    private final Keycloak keycloak;
    private final String realmName;
    private final KeycloakUserService keycloakUserService;

    public KeycloakRoleServiceImpl(Keycloak keycloak, @Value("${keycloak.realm}") String realmName,
                                   KeycloakUserService keycloakUserService) {
        this.keycloak = keycloak;
        this.realmName = realmName;
        this.keycloakUserService = keycloakUserService;
    }

    @Override
    public void assignRole(String userId, String roleName) {
        UserResource userResources = keycloakUserService.getUserResources(userId);

        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();

        userResources.roles().realmLevel().add(Collections.singletonList(representation));
    }

    @Override
    public void reassignRole(String userId, String roleName) {

    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realmName).roles();
    }

}
