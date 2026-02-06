package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.TeacherRole;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a teacher is assigned to a class.
 */
public record TeacherAssignedToClass(
        AssociationId assignmentId, UserId teacherId, ClassId classId, TeacherRole role, Instant occurredAt) {
    public TeacherAssignedToClass {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
