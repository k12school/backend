package com.k12.platform.interfaces.rest.dto;

/**
 * REST response DTO for successful login.
 */
public record LoginResponse(String token, UserResponse user) {
}
