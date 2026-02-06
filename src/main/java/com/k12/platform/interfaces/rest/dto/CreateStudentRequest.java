package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for creating a Student.
 */
public record CreateStudentRequest(
        @NotNull(message = "First name is required") @JsonProperty("first_name") String firstName,
        @NotNull(message = "Last name is required") @JsonProperty("last_name") String lastName,
        @NotNull(message = "Date of birth is required") @JsonProperty("date_of_birth") String dateOfBirth,
        @NotNull(message = "Grade level is required") @Pattern(regexp = "^(K|0|1[0-2]|[1-9])$", message = "Grade level must be K or 0-12")
                @JsonProperty("grade_level")
                String gradeLevel,
        @NotNull(message = "Class ID is required") @JsonProperty("class_id") String classId,
        @JsonProperty("student_number") String studentNumber,
        @JsonProperty("enrollment_date") String enrollmentDate) {}
