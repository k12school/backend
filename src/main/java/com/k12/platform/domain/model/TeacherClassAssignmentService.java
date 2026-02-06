package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.TeacherRole;
import com.k12.platform.domain.model.valueobjects.UserId;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

/**
 * Domain service for Teacher-Class assignment operations.
 */
@ApplicationScoped
public class TeacherClassAssignmentService {

    private final TeacherClassAssignmentRepository repository;

    public TeacherClassAssignmentService(TeacherClassAssignmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new teacher-class assignment with uniqueness validation.
     */
    public TeacherClassAssignment assign(UserId teacherId, ClassId classId, TeacherRole role, LocalDate assignedDate) {
        // Check for uniqueness
        if (repository.existsByTeacherIdAndClassId(teacherId, classId)) {
            throw new IllegalStateException(
                    "Assignment already exists between teacher " + teacherId + " and class " + classId);
        }

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);

        repository.save(assignment);
        return assignment;
    }
}
