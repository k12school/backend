package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import java.time.Instant;

/**
 * Domain event emitted when a student advances to the next grade.
 */
public record StudentAdvanced(
        StudentId studentId, GradeLevel previousGradeLevel, GradeLevel newGradeLevel, Instant occurredAt) {
    public StudentAdvanced {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
