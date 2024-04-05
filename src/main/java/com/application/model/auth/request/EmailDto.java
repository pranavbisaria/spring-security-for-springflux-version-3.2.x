package com.application.model.auth.request;

import jakarta.validation.constraints.Email;

public record EmailDto (
        @Email(message = "Email address is not valid")
        String email
) {}
