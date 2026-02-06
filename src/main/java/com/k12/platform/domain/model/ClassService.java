package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

/**
 * Domain service for Class operations.
 * Ensures business rules around class creation.
 */
@ApplicationScoped
public class ClassService {

    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    /**
     * Create a new class with uniqueness validation.
     */
    public Class createClass(ClassName name, GradeLevel gradeLevel, AcademicYear academicYear) {
        // Check for uniqueness
        if (classRepository.existsByNameAndGradeLevelAndAcademicYear(
                name.value(), gradeLevel.value(), academicYear.value())) {
            throw new IllegalStateException("Class with name '" + name + "' already exists for grade "
                    + gradeLevel.displayValue() + " in academic year " + academicYear);
        }

        Class clazz = Class.create(name, gradeLevel, academicYear);
        classRepository.save(clazz);
        return clazz;
    }

    /**
     * Reconstitute a class from persistence.
     */
    public Class reconstituteClass(
            ClassId classId,
            ClassName name,
            GradeLevel gradeLevel,
            AcademicYear academicYear,
            Instant createdAt,
            Instant updatedAt) {
        return Class.reconstitute(classId, name, gradeLevel, academicYear, createdAt, updatedAt);
    }
}
