package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import java.time.Instant;

/**
 * Domain event emitted when a student is registered.
 */
public record StudentRegistered(StudentId studentId, GradeLevel gradeLevel, Instant occurredAt) {
    public StudentRegistered {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
