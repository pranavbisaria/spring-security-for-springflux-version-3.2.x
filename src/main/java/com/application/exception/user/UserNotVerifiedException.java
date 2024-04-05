package com.application.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotVerifiedException extends ResponseStatusException {
    public UserNotVerifiedException() {
        super(HttpStatus.FORBIDDEN, "User is not verified!");
    }
}
