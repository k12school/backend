package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a parent-child relationship type.
 * Immutable and self-validating.
 */
public final class RelationshipType {

    private final String value;

    private RelationshipType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Relationship type cannot be null or blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Relationship type cannot exceed 50 characters");
        }
        this.value = value.trim();
    }

    public static RelationshipType of(String type) {
        return new RelationshipType(type);
    }

    // Common relationship types
    public static RelationshipType father() {
        return new RelationshipType("Father");
    }

    public static RelationshipType mother() {
        return new RelationshipType("Mother");
    }

    public static RelationshipType guardian() {
        return new RelationshipType("Guardian");
    }

    public static RelationshipType stepfather() {
        return new RelationshipType("Stepfather");
    }

    public static RelationshipType stepmother() {
        return new RelationshipType("Stepmother");
    }

    public static RelationshipType grandparent() {
        return new RelationshipType("Grandparent");
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipType that = (RelationshipType) o;
        return Objects.equals(value, that.value);
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
