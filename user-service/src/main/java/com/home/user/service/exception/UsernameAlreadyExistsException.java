package com.home.user.service.exception;

public class UsernameAlreadyExistsException extends DataConflictException {
    public UsernameAlreadyExistsException(String username) {
        super("USERNAME_EXISTS", "Username already taken: " + username);
    }
}
