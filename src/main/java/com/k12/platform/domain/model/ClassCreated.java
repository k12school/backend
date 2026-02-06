package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AcademicYear;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.ClassName;
import com.k12.platform.domain.model.valueobjects.GradeLevel;
import java.time.Instant;

/**
 * Domain event emitted when a class is created.
 */
public record ClassCreated(
        ClassId classId, ClassName name, GradeLevel gradeLevel, AcademicYear academicYear, Instant occurredAt) {
    public ClassCreated {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
