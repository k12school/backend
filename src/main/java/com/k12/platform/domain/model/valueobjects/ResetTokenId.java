package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a password reset token identifier.
 * Immutable and UUID-based.
 */
public final class ResetTokenId {

    private final UUID value;

    private ResetTokenId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ResetTokenId cannot be null");
        }
        this.value = value;
    }

    public static ResetTokenId of(UUID uuid) {
        return new ResetTokenId(uuid);
    }

    public static ResetTokenId of(String uuid) {
        return new ResetTokenId(UUID.fromString(uuid));
    }

    public static ResetTokenId generate() {
        return new ResetTokenId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetTokenId resetTokenId = (ResetTokenId) o;
        return Objects.equals(value, resetTokenId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
