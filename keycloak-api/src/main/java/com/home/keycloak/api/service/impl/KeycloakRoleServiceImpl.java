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
        try {
            UserResource userResource = keycloakUserService.getUserResources(userId);
            RolesResource rolesResource = getRolesResource();


            RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
            if (role == null) {
                throw new IllegalArgumentException("Role " + roleName + " does not exist!");
            }

            userResource.roles().realmLevel().add(Collections.singletonList(role));

        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role: " + e.getMessage(), e);
        }
    }

    @Override
    public void reassignRole(String userId, String roleName) {

    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realmName).roles();
    }

}
