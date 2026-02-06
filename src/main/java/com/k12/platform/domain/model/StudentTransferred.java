package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import java.time.Instant;

/**
 * Domain event emitted when a student is transferred to a different grade.
 */
public record StudentTransferred(StudentId studentId, GradeLevel fromGrade, GradeLevel toGrade, Instant occurredAt) {
    public StudentTransferred {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
