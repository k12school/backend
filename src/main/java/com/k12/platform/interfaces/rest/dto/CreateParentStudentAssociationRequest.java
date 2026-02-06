package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a Parent-Student association.
 */
public record CreateParentStudentAssociationRequest(
        @NotNull(message = "Parent ID is required") @JsonProperty("parent_id") String parentId,
        @NotNull(message = "Student ID is required") @JsonProperty("student_id") String studentId,
        @NotNull(message = "Relationship type is required") @Size(max = 50, message = "Relationship type cannot exceed 50 characters")
                @JsonProperty("relationship_type")
                String relationshipType,
        @JsonProperty("is_primary_contact") boolean isPrimaryContact) {}
