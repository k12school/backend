package com.k12.platform.domain.service;

/**
 * Domain service for password validation.
 * Pure Java, no framework dependencies.
 */
public final class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    private PasswordValidator() {
        // Utility class
    }

    /**
     * Validates password strength.
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public static void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters");
        }
    }

    /**
     * Check if password meets requirements without throwing.
     */
    public static boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
