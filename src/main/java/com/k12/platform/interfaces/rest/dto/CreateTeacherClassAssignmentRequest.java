package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a Teacher-Class assignment.
 */
public record CreateTeacherClassAssignmentRequest(
        @NotNull(message = "Teacher ID is required") @JsonProperty("teacher_id") String teacherId,
        @NotNull(message = "Class ID is required") @JsonProperty("class_id") String classId,
        @NotNull(message = "Role is required") @JsonProperty("role") String role,
        @JsonProperty("assigned_date") String assignedDate) {}
