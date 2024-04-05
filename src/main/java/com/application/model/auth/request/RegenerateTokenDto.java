package com.application.model.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RegenerateTokenDto(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) { }
