package com.application.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidOTPException extends ResponseStatusException {
    public InvalidOTPException() {
        super(HttpStatus.BAD_REQUEST, "OTP provided is Invalid!");
    }
}
