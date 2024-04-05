package com.application.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring")
@Validated
public class PropertiesConfig {
    private Jwt jwt;
    private MailConfig mail;
    private Cors cors;

    @Getter
    public static class Jwt {
        @NotBlank
        private String issuer;
        @NotNull
        private Token validity;
        @NotNull
        private String secret;

        @Getter
        public static class Token {
            @Positive
            private Long accessTokenValidity;
            @Positive
            private Long refreshTokenValidity;
        }
    }

    @Getter
    public static class MailConfig {
        private String host;
        private int port;
        private String username;
        private String password;
    }

    @Getter
    public static class Cors {
        private List<String> allowedOrigins;
    }
}
