package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.StudentId;
import java.time.Instant;

/**
 * Domain event emitted when student information is updated.
 */
public record StudentInfoUpdated(StudentId studentId, Instant occurredAt) {
    public StudentInfoUpdated {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
