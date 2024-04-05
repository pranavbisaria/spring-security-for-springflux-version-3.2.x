package com.application.model.auth.response;

import java.time.LocalDateTime;

public record AuthenticatedUser(
        String accessToken,
        String refreshToken,
        LocalDateTime generatedAt,
        String username
) {
    public AuthenticatedUser(String accessToken, String refreshToken, String username) {
        this(accessToken, refreshToken, LocalDateTime.now(), username);
    }
}
