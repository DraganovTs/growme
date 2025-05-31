package com.home.keycloak.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${JWT_AUTH_RESOURCE_ID}")
    private String resourceId = "authenticationClientId";


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        Collection<String> allRoles = new ArrayList<>();
        Collection<String> resourceRoles;
        Collection<String> realmRoles ;

        if(resourceAccess != null && resourceAccess.get("account") != null){
            Map<String,Object> account =  (Map<String,Object>) resourceAccess.get("account");
            if(account.containsKey("roles") ){
                resourceRoles = (Collection<String>) account.get("roles");
                allRoles.addAll(resourceRoles);
            }
        }

        if(realmAccess != null && realmAccess.containsKey("roles")){
            realmRoles = (Collection<String>) realmAccess.get("roles");
            allRoles.addAll(realmRoles);
        }
        if (allRoles.isEmpty() || !Objects.equals(resourceId,jwt.getClaim("azp")) ) {

            return Set.of();
        }

        return allRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${KEYCLOAK_URL}") String keycloakUrl,
            @Value("${KEYCLOAK_REALM}") String realm) {
        return JwtDecoders.fromIssuerLocation(keycloakUrl + "/realms/" + realm);
    }
}

