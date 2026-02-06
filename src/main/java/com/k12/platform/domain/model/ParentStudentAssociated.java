package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.RelationshipType;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a parent is associated with a student.
 */
public record ParentStudentAssociated(
        AssociationId associationId,
        UserId parentId,
        StudentId studentId,
        RelationshipType relationshipType,
        Instant occurredAt) {
    public ParentStudentAssociated {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
