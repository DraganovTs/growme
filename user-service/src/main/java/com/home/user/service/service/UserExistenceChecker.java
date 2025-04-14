package com.home.user.service.service;

public interface UserExistenceChecker {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
