package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for password reset request.
 */
public record PasswordResetRequestRequest(
        @NotNull(message = "Email is required") @Email(message = "Email must be valid") @JsonProperty("email")
                String email) {}
