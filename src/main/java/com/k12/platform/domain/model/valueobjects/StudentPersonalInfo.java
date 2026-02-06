package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a student's personal information.
 * Immutable.
 */
public final class StudentPersonalInfo {

    private final String firstName;
    private final String lastName;
    private final String dateOfBirth; // ISO date format or null

    private StudentPersonalInfo(String firstName, String lastName, String dateOfBirth) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth; // Optional
    }

    public static StudentPersonalInfo of(String firstName, String lastName, String dateOfBirth) {
        return new StudentPersonalInfo(firstName, lastName, dateOfBirth);
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String dateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentPersonalInfo that = (StudentPersonalInfo) o;
        return Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(dateOfBirth, that.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dateOfBirth);
    }
}
