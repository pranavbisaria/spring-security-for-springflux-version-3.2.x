package com.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition (
        info = @Info(
                title = "Spring Flux Security Demo API Documentation",
                version = "1.0",
                description = "Complete API documentation for spring application."),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local server"
                )
        }
)
@SecurityScheme(
        name = "Bearer Token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description  = "JWT Access Token for authentication"
)
public class SwaggerConfig {
}
