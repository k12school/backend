package com.k12.platform.interfaces.rest.dto;

/**
 * REST request DTO for login.
 */
public record LoginRequest(String email, String password) {
}
