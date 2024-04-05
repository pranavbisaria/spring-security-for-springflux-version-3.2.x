package com.application.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRefreshTokenException  extends ResponseStatusException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Refresh token provided is invalid!");
    }
}
