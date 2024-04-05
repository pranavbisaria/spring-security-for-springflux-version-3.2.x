package com.application.model.auth.request;

import jakarta.validation.constraints.Email;

public record LoginUserDto (
        @Email(message = "Email address is not valid")
        String email,
        String password
) {}
