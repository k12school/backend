package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a student identifier.
 * Immutable and UUID-based.
 */
public final class StudentId {

    private final UUID value;

    private StudentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("StudentId cannot be null");
        }
        this.value = value;
    }

    public static StudentId of(UUID uuid) {
        return new StudentId(uuid);
    }

    public static StudentId of(String uuid) {
        return new StudentId(UUID.fromString(uuid));
    }

    public static StudentId generate() {
        return new StudentId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentId studentId = (StudentId) o;
        return Objects.equals(value, studentId.value);
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
