package com.k12.platform.domain.model.valueobjects;

import com.k12.platform.domain.model.exceptions.InvalidGradeLevelException;
import java.util.Objects;

/**
 * Value object representing a grade level (K-12).
 * Stored as INTEGER in database: 0=K, 1-12=grade levels.
 * Immutable and self-validating.
 */
public final class GradeLevel {

    private final int value;

    private GradeLevel(int value) {
        if (value < 0 || value > 12) {
            throw new InvalidGradeLevelException("Invalid grade level: " + value + ". Must be 0-12 (0=K).");
        }
        this.value = value;
    }

    /**
     * Create grade level from integer (0-12).
     */
    public static GradeLevel of(int grade) {
        return new GradeLevel(grade);
    }

    /**
     * Create grade level from string ("K" -> 0, "1"-"12").
     */
    public static GradeLevel fromString(String grade) {
        if ("K".equalsIgnoreCase(grade)) {
            return new GradeLevel(0);
        }
        try {
            int gradeNum = Integer.parseInt(grade);
            return new GradeLevel(gradeNum);
        } catch (NumberFormatException e) {
            throw new InvalidGradeLevelException("Invalid grade level string: " + grade);
        }
    }

    /**
     * Get integer value (0-12).
     */
    public int value() {
        return value;
    }

    /**
     * Get display string ("K" for 0, "1"-"12" otherwise).
     */
    public String displayValue() {
        return value == 0 ? "K" : String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeLevel gradeLevel = (GradeLevel) o;
        return value == gradeLevel.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return displayValue();
    }
}
