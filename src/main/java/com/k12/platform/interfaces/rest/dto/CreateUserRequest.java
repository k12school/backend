package com.k12.platform.interfaces.rest.dto;

/**
 * REST request DTO for creating a user.
 */
public record CreateUserRequest(String email, String password, String firstName, String lastName) {
}
