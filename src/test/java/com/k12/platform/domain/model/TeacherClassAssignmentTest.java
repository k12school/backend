package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.TeacherRole;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TeacherClassAssignment aggregate.
 * Target: 100% coverage
 */
@DisplayName("TeacherClassAssignment Aggregate Tests")
class TeacherClassAssignmentTest {

    @Test
    @DisplayName("Should create teacher-class assignment")
    void shouldCreateAssignment() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.homeroomTeacher();
        LocalDate assignedDate = LocalDate.of(2024, 9, 1);

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);

        assertNotNull(assignment.assignmentId());
        assertEquals(teacherId, assignment.teacherId());
        assertEquals(classId, assignment.classId());
        assertEquals(role, assignment.role());
        assertEquals(assignedDate, assignment.assignedDate());
        assertNotNull(assignment.createdAt());
    }

    @Test
    @DisplayName("Should record TeacherAssignedToClass event on creation")
    void shouldRecordTeacherAssignedToClassEvent() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.subjectTeacher();
        LocalDate assignedDate = LocalDate.now();

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);

        var domainEvents = assignment.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof TeacherAssignedToClass);
    }

    @Test
    @DisplayName("Should default assigned date to today")
    void shouldDefaultAssignedDateToToday() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.assistantTeacher();

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, null);

        assertEquals(LocalDate.now(), assignment.assignedDate());
    }

    @Test
    @DisplayName("Should reconstitute assignment from persistence")
    void shouldReconstituteAssignmentFromPersistence() {
        AssociationId assignmentId = AssociationId.generate();
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.assistantTeacher();
        LocalDate assignedDate = LocalDate.of(2024, 9, 1);
        Instant createdAt = Instant.now().minusSeconds(3600);

        TeacherClassAssignment assignment =
                TeacherClassAssignment.reconstitute(assignmentId, teacherId, classId, role, assignedDate, createdAt);

        assertEquals(assignmentId, assignment.assignmentId());
        assertEquals(teacherId, assignment.teacherId());
        assertEquals(classId, assignment.classId());
        assertEquals(role, assignment.role());
        assertEquals(assignedDate, assignment.assignedDate());
        assertEquals(0, assignment.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.homeroomTeacher();

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, LocalDate.now());

        var domainEvents = assignment.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof TeacherAssignedToClass);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.subjectTeacher();

        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, LocalDate.now());
        var domainEvents = assignment.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should handle all teacher roles")
    void shouldHandleAllTeacherRoles() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        LocalDate assignedDate = LocalDate.now();

        TeacherRole[] roles = {
            TeacherRole.homeroomTeacher(), TeacherRole.subjectTeacher(), TeacherRole.assistantTeacher()
        };

        for (TeacherRole role : roles) {
            TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);
            assertEquals(role, assignment.role());
        }
    }

    @Test
    @DisplayName("Should have unique assignment IDs")
    void shouldHaveUniqueAssignmentIds() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.homeroomTeacher();
        LocalDate assignedDate = LocalDate.now();

        TeacherClassAssignment assignment1 = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);
        TeacherClassAssignment assignment2 = TeacherClassAssignment.create(teacherId, classId, role, assignedDate);

        assertNotEquals(assignment1.assignmentId(), assignment2.assignmentId());
    }

    @Test
    @DisplayName("Should handle different assigned dates")
    void shouldHandleDifferentAssignedDates() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.subjectTeacher();

        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 15);
        LocalDate date3 = LocalDate.of(2024, 9, 1);

        TeacherClassAssignment assignment1 = TeacherClassAssignment.create(teacherId, classId, role, date1);
        TeacherClassAssignment assignment2 = TeacherClassAssignment.create(teacherId, classId, role, date2);
        TeacherClassAssignment assignment3 = TeacherClassAssignment.create(teacherId, classId, role, date3);

        assertEquals(date1, assignment1.assignedDate());
        assertEquals(date2, assignment2.assignedDate());
        assertEquals(date3, assignment3.assignedDate());
    }

    @Test
    @DisplayName("Should handle assignments with different teachers")
    void shouldHandleAssignmentsWithDifferentTeachers() {
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.homeroomTeacher();
        LocalDate assignedDate = LocalDate.now();

        UserId teacher1 = UserId.generate();
        UserId teacher2 = UserId.generate();

        TeacherClassAssignment assignment1 = TeacherClassAssignment.create(teacher1, classId, role, assignedDate);
        TeacherClassAssignment assignment2 = TeacherClassAssignment.create(teacher2, classId, role, assignedDate);

        assertEquals(teacher1, assignment1.teacherId());
        assertEquals(teacher2, assignment2.teacherId());
    }

    @Test
    @DisplayName("Should handle assignments with different classes")
    void shouldHandleAssignmentsWithDifferentClasses() {
        UserId teacherId = UserId.generate();
        TeacherRole role = TeacherRole.subjectTeacher();
        LocalDate assignedDate = LocalDate.now();

        ClassId class1 = ClassId.generate();
        ClassId class2 = ClassId.generate();

        TeacherClassAssignment assignment1 = TeacherClassAssignment.create(teacherId, class1, role, assignedDate);
        TeacherClassAssignment assignment2 = TeacherClassAssignment.create(teacherId, class2, role, assignedDate);

        assertEquals(class1, assignment1.classId());
        assertEquals(class2, assignment2.classId());
    }

    @Test
    @DisplayName("Should set createdAt timestamp on creation")
    void shouldSetCreatedAtTimestampOnCreation() {
        UserId teacherId = UserId.generate();
        ClassId classId = ClassId.generate();
        TeacherRole role = TeacherRole.assistantTeacher();

        Instant beforeCreation = Instant.now();
        TeacherClassAssignment assignment = TeacherClassAssignment.create(teacherId, classId, role, LocalDate.now());
        Instant afterCreation = Instant.now();

        assertNotNull(assignment.createdAt());
        assertTrue(assignment.createdAt().isAfter(beforeCreation.minusMillis(100)));
        assertTrue(assignment.createdAt().isBefore(afterCreation.plusMillis(100)));
    }
}
