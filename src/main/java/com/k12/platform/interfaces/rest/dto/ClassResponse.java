package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Class.
 */
public record ClassResponse(
        @JsonProperty("class_id") String classId,
        @JsonProperty("name") String name,
        @JsonProperty("grade_level") String gradeLevel,
        @JsonProperty("academic_year") String academicYear) {}
