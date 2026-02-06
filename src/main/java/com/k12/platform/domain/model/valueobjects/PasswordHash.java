package com.k12.platform.domain.model.valueobjects;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Value object representing a hashed password.
 * Immutable and one-way (cannot retrieve original password).
 */
public final class PasswordHash {

    private final String value;

    private PasswordHash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * Creates a PasswordHash from an already hashed string (e.g., from database).
     */
    public static PasswordHash of(String hashedValue) {
        return new PasswordHash(hashedValue);
    }

    /**
     * Hashes a plain text password using bcrypt.
     */
    public static PasswordHash hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Plain password cannot be null or blank");
        }
        String hashed = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
        return new PasswordHash(hashed);
    }

    /**
     * Verifies if the plain text password matches this hash.
     */
    public boolean verify(String plainPassword) {
        if (plainPassword == null) {
            return false;
        }
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), value);
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "******"; // Never log actual hash
    }
}
