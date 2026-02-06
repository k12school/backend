package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for password reset confirmation.
 */
public record PasswordResetConfirmRequest(
        @NotNull(message = "Token is required") @JsonProperty("token") String token,
        @NotNull(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters")
                @JsonProperty("new_password")
                String newPassword) {}
