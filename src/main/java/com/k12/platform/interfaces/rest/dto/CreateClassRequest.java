package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a Class.
 */
public record CreateClassRequest(
        @NotNull(message = "Class name is required") @Size(max = 100, message = "Class name cannot exceed 100 characters")
                @JsonProperty("name")
                String name,
        @NotNull(message = "Grade level is required") @Pattern(regexp = "^(K|0|1[0-2]|[1-9])$", message = "Grade level must be K or 0-12")
                @JsonProperty("grade_level")
                String gradeLevel,
        @NotNull(message = "Academic year is required") @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic year must be in format YYYY-YYYY")
                @JsonProperty("academic_year")
                String academicYear) {}
