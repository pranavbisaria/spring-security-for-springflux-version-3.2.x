package com.application.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistWithUserNameException extends ResponseStatusException {
    public UserAlreadyExistWithUserNameException() {
        super(HttpStatus.CONFLICT, "User already exists with entered username");
    }
}