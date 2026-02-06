package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an association identifier.
 * Immutable and UUID-based.
 */
public final class AssociationId {

    private final UUID value;

    private AssociationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AssociationId cannot be null");
        }
        this.value = value;
    }

    public static AssociationId of(UUID uuid) {
        return new AssociationId(uuid);
    }

    public static AssociationId of(String uuid) {
        return new AssociationId(UUID.fromString(uuid));
    }

    public static AssociationId generate() {
        return new AssociationId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssociationId associationId = (AssociationId) o;
        return Objects.equals(value, associationId.value);
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
