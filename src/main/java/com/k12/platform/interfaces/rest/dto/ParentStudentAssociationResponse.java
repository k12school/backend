package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Parent-Student association.
 */
public record ParentStudentAssociationResponse(
        @JsonProperty("association_id") String associationId,
        @JsonProperty("parent_id") String parentId,
        @JsonProperty("student_id") String studentId,
        @JsonProperty("relationship_type") String relationshipType,
        @JsonProperty("is_primary_contact") boolean isPrimaryContact) {}
