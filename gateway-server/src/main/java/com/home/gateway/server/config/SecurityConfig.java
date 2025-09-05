package com.home.gateway.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;




    @Configuration
//    @EnableWebFluxSecurity
    public class SecurityConfig {

//        @Bean
//        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//            http
//                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                    .cors(cors -> {}) // enable CORS without deprecated and()
//                    .authorizeExchange(exchanges -> exchanges
//                            .pathMatchers("/actuator/**", "/contactSupport").permitAll()
//                            .pathMatchers("/growme/auth/**").permitAll()
//                            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                            .pathMatchers(HttpMethod.POST, "/growme/users/sync").permitAll()
//                            .pathMatchers(HttpMethod.POST, "/growme/users/profile-complete").permitAll()
//                            .anyExchange().authenticated()
//                    )
//                    .oauth2ResourceServer(oauth2 -> oauth2
//                            .jwt(jwt -> jwt.jwtAuthenticationConverter(
//                                    new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter())
//                            ))
//                    );
//            return http.build();
//        }
//
//        /**
//         * Global CORS filter for Gateway.
//         */
//        @Bean
//        public CorsWebFilter corsWebFilter() {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:4200"));
//            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//            config.setAllowedHeaders(Arrays.asList(
//                    "Authorization", "Content-Type", "Accept", "X-Requested-With",
//                    "Access-Control-Request-Method", "Access-Control-Request-Headers"
//            ));
//            config.setAllowCredentials(true);
//            config.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
//
//            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//            source.registerCorsConfiguration("/**", config);
//
//            return new CorsWebFilter(source);
//        }
//
//        /**
//         * Custom JWT converter to map Keycloak roles to Spring Security roles.
//         */
//        @Bean
//        public JwtAuthenticationConverter jwtAuthenticationConverter() {
//            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//                if (realmAccess != null && realmAccess.containsKey("roles")) {
//                    @SuppressWarnings("unchecked")
//                    List<String> roles = (List<String>) realmAccess.get("roles");
//                    return roles.stream()
//                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
//                            .collect(Collectors.toList());
//                }
//                return Collections.emptyList();
//            });
//            return converter;
//        }
    }