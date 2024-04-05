package com.application.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistException extends ResponseStatusException {
    public UserAlreadyExistException() {
        super(HttpStatus.CONFLICT, "User already exists with entered email");
    }
}
