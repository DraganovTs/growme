package com.home.keycloak.api.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${KEYCLOAK_URL}")
    private String serverUrl  ;

    @Value("${KEYCLOAK_REALM}")
    private String realm ;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId ;

    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String clientSecret ;

    @Bean
    public Keycloak keycloak(){

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
