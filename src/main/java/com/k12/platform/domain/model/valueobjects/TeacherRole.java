package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a teacher's role in a class.
 * Immutable and self-validating.
 */
public final class TeacherRole {

    private final String value;

    private TeacherRole(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Teacher role cannot be null or blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Teacher role cannot exceed 50 characters");
        }
        this.value = value.trim();
    }

    public static TeacherRole of(String role) {
        return new TeacherRole(role);
    }

    // Common teacher roles
    public static TeacherRole homeroomTeacher() {
        return new TeacherRole("Homeroom Teacher");
    }

    public static TeacherRole subjectTeacher() {
        return new TeacherRole("Subject Teacher");
    }

    public static TeacherRole assistantTeacher() {
        return new TeacherRole("Assistant Teacher");
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherRole teacherRole = (TeacherRole) o;
        return Objects.equals(value, teacherRole.value);
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
