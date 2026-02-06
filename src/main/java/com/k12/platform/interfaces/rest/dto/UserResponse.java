package com.k12.platform.interfaces.rest.dto;

/**
 * REST response DTO for user data.
 */
public record UserResponse(String id, String email, String role, String firstName, String lastName, boolean isActive) {
}
