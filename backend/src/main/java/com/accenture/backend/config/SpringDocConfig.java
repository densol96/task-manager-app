package com.accenture.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        description = "JWT Authentication"
)
@OpenAPIDefinition(
        info = @Info(
                title = "API Documentation",
                version = "1.0.0",
                description = "API endpoints for the application."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
public class SpringDocConfig {

}
