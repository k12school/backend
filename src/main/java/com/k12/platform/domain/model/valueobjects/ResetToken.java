package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a password reset token string.
 * Immutable.
 */
public final class ResetToken {

    private final String value;

    private ResetToken(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Reset token cannot be null or blank");
        }
        this.value = value;
    }

    public static ResetToken of(String token) {
        return new ResetToken(token);
    }

    public static ResetToken generate() {
        return new ResetToken(java.util.UUID.randomUUID().toString());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetToken resetToken = (ResetToken) o;
        return Objects.equals(value, resetToken.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
