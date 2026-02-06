package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a class name.
 * Immutable and self-validating.
 */
public final class ClassName {

    private final String value;

    private ClassName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Class name cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Class name cannot exceed 100 characters");
        }
        this.value = value.trim();
    }

    public static ClassName of(String name) {
        return new ClassName(name);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassName className = (ClassName) o;
        return Objects.equals(value, className.value);
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
