package com.application.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OTPExpiredException extends ResponseStatusException {
    public OTPExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "OTP provided is expired. Please request a new OTP.");
    }
}
