package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Student.
 */
public record StudentResponse(
        @JsonProperty("student_id") String studentId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("date_of_birth") String dateOfBirth,
        @JsonProperty("grade_level") String gradeLevel,
        @JsonProperty("class_id") String classId,
        @JsonProperty("student_number") String studentNumber,
        @JsonProperty("enrollment_date") String enrollmentDate) {}
