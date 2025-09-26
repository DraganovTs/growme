//package com.home.preorder.service.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    private static final String MAPPING_PATTERN = "/**";
//    private static final String[] ALLOWED_ORIGINS = {
//            "http://localhost:4200"
//    };
//    private static final String[] ALLOWED_METHODS = {
//            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
//    };
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping(MAPPING_PATTERN)
//                .allowedOrigins(ALLOWED_ORIGINS)
//                .allowedMethods(ALLOWED_METHODS)
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}
