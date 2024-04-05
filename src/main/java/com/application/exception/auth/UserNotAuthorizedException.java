package com.application.exception.auth;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotAuthorizedException extends ResponseStatusException {
    public UserNotAuthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "User not authorized!");
    }
}
