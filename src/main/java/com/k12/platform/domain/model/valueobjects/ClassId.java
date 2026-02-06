package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a class identifier.
 * Immutable and UUID-based.
 */
public final class ClassId {

    private final UUID value;

    private ClassId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ClassId cannot be null");
        }
        this.value = value;
    }

    public static ClassId of(UUID uuid) {
        return new ClassId(uuid);
    }

    public static ClassId of(String uuid) {
        return new ClassId(UUID.fromString(uuid));
    }

    public static ClassId generate() {
        return new ClassId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassId classId = (ClassId) o;
        return Objects.equals(value, classId.value);
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
