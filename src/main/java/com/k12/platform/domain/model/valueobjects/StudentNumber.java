package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a student number (optional school-assigned identifier).
 * Immutable.
 */
public final class StudentNumber {

    private final String value;

    private StudentNumber(String value) {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("Student number cannot be blank");
        }
        this.value = value;
    }

    public static StudentNumber of(String number) {
        return new StudentNumber(number);
    }

    public static StudentNumber empty() {
        return new StudentNumber(null);
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentNumber that = (StudentNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value != null ? value : "[no student number]";
    }
}
