package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Teacher-Class assignment aggregate root.
 * Represents the assignment of a teacher to a class.
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 */
public final class TeacherClassAssignment {

    private final AssociationId assignmentId;
    private final UserId teacherId;
    private final ClassId classId;
    private final TeacherRole role;
    private final LocalDate assignedDate;
    private final Instant createdAt;

    private TeacherClassAssignment(
            AssociationId assignmentId, UserId teacherId, ClassId classId, TeacherRole role, LocalDate assignedDate) {
        this.assignmentId = assignmentId;
        this.teacherId = teacherId;
        this.classId = classId;
        this.role = role;
        this.assignedDate = assignedDate != null ? assignedDate : LocalDate.now();
        this.createdAt = Instant.now();
    }

    /**
     * Factory method to create a new teacher-class assignment.
     */
    public static TeacherClassAssignment create(
            UserId teacherId, ClassId classId, TeacherRole role, LocalDate assignedDate) {
        TeacherClassAssignment assignment =
                new TeacherClassAssignment(AssociationId.generate(), teacherId, classId, role, assignedDate);

        assignment.recordDomainEvent(new TeacherAssignedToClass(
                assignment.assignmentId, assignment.teacherId, assignment.classId, assignment.role, null));

        return assignment;
    }

    /**
     * Factory method to reconstitute from persistence.
     */
    public static TeacherClassAssignment reconstitute(
            AssociationId assignmentId,
            UserId teacherId,
            ClassId classId,
            TeacherRole role,
            LocalDate assignedDate,
            Instant createdAt) {
        TeacherClassAssignment assignment =
                new TeacherClassAssignment(assignmentId, teacherId, classId, role, assignedDate);
        return assignment;
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

    // Getters
    public AssociationId assignmentId() {
        return assignmentId;
    }

    public UserId teacherId() {
        return teacherId;
    }

    public ClassId classId() {
        return classId;
    }

    public TeacherRole role() {
        return role;
    }

    public LocalDate assignedDate() {
        return assignedDate;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
