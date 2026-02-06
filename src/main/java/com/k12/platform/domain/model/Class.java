package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * Class aggregate root.
 * Represents a school class (e.g., "Grade 5-A", "Room 101").
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 */
@Getter
public final class Class {

    private final ClassId classId;
    private final ClassName name;
    private final GradeLevel gradeLevel;
    private final AcademicYear academicYear;
    private final Instant createdAt;
    private Instant updatedAt;

    private Class(
            ClassId classId,
            ClassName name,
            GradeLevel gradeLevel,
            AcademicYear academicYear,
            Instant createdAt,
            Instant updatedAt) {
        this.classId = classId;
        this.name = name;
        this.gradeLevel = gradeLevel;
        this.academicYear = academicYear;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    /**
     * Factory method to create a new class.
     */
    public static Class create(ClassName name, GradeLevel gradeLevel, AcademicYear academicYear) {
        Instant now = Instant.now();
        Class clazz = new Class(ClassId.generate(), name, gradeLevel, academicYear, now, now);

        clazz.recordDomainEvent(
                new ClassCreated(clazz.classId, clazz.name, clazz.gradeLevel, clazz.academicYear, null));

        return clazz;
    }

    /**
     * Factory method to reconstitute class from persistence.
     */
    public static Class reconstitute(
            ClassId classId,
            ClassName name,
            GradeLevel gradeLevel,
            AcademicYear academicYear,
            Instant createdAt,
            Instant updatedAt) {
        return new Class(classId, name, gradeLevel, academicYear, createdAt, updatedAt);
    }

    private void recordDomainEvent(Object event) {
        this.domainEvents.add(event);
    }

    private final List<Object> domainEvents = new ArrayList<>();

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
