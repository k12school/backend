package com.k12.platform.domain.model.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an academic year.
 * Format: "YYYY-YYYY" (e.g., "2024-2025")
 * Must be within ±2 years of current academic year.
 * Immutable and self-validating.
 */
public final class AcademicYear {

    private static final Pattern PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");
    private final String value;

    private AcademicYear(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Academic year cannot be null or blank");
        }

        String trimmed = value.trim();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Academic year must be in format YYYY-YYYY (e.g., 2024-2025)");
        }

        // Validate the years make sense (consecutive)
        String[] parts = trimmed.split("-");
        int startYear = Integer.parseInt(parts[0]);
        int endYear = Integer.parseInt(parts[1]);

        if (endYear != startYear + 1) {
            throw new IllegalArgumentException("Academic year must span consecutive years (e.g., 2024-2025)");
        }

        // Validate within reasonable range (current year ± 2)
        int currentYear = java.time.Year.now().getValue();
        if (startYear < currentYear - 2 || startYear > currentYear + 2) {
            throw new IllegalArgumentException("Academic year must be within ±2 years of current year");
        }

        this.value = trimmed;
    }

    public static AcademicYear of(String year) {
        return new AcademicYear(year);
    }

    public static AcademicYear current() {
        int currentYear = java.time.Year.now().getValue();
        // Academic year typically starts in fall, so current academic year
        // is previous year to current year if we're in early months
        int startYear = currentYear - 1;
        return new AcademicYear(startYear + "-" + currentYear);
    }

    public String value() {
        return value;
    }

    /**
     * Get the start year of the academic year.
     */
    public int startYear() {
        return Integer.parseInt(value.split("-")[0]);
    }

    /**
     * Get the end year of the academic year.
     */
    public int endYear() {
        return Integer.parseInt(value.split("-")[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicYear that = (AcademicYear) o;
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
