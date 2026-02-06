package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Teacher-Class assignment.
 */
public record TeacherClassAssignmentResponse(
        @JsonProperty("assignment_id") String assignmentId,
        @JsonProperty("teacher_id") String teacherId,
        @JsonProperty("class_id") String classId,
        @JsonProperty("role") String role,
        @JsonProperty("assigned_date") String assignedDate) {}
