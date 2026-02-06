package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.TeacherRole;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for TeacherClassAssignmentService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("TeacherClassAssignmentService Tests")
class TeacherClassAssignmentServiceTest {

    @Mock
    TeacherClassAssignmentRepository repository;

    TeacherClassAssignmentService service;

    private UserId teacherId;
    private ClassId classId;
    private TeacherRole role;
    private LocalDate assignedDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TeacherClassAssignmentService(repository);
        teacherId = UserId.generate();
        classId = ClassId.generate();
        role = TeacherRole.homeroomTeacher();
        assignedDate = LocalDate.of(2024, 9, 1);
        doNothing().when(repository).save(any(TeacherClassAssignment.class));
    }

    @Test
    @DisplayName("Should create assignment successfully")
    void shouldCreateAssignmentSuccessfully() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

        TeacherClassAssignment result = service.assign(teacherId, classId, role, assignedDate);

        assertNotNull(result);
        assertEquals(teacherId, result.teacherId());
        assertEquals(classId, result.classId());
        assertEquals(role, result.role());
        assertEquals(assignedDate, result.assignedDate());
        verify(repository).existsByTeacherIdAndClassId(teacherId, classId);
        verify(repository).save(any(TeacherClassAssignment.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when assignment already exists")
    void shouldThrowIllegalStateExceptionWhenAssignmentExists() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(true);

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> service.assign(teacherId, classId, role, assignedDate));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(repository).existsByTeacherIdAndClassId(teacherId, classId);
        verify(repository, never()).save(any(TeacherClassAssignment.class));
    }

    @Test
    @DisplayName("Should not save when assignment already exists")
    void shouldNotSaveWhenAssignmentExists() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.assign(teacherId, classId, role, assignedDate));

        verify(repository, never()).save(any(TeacherClassAssignment.class));
    }

    @Test
    @DisplayName("Should check uniqueness before creating assignment")
    void shouldCheckUniquenessBeforeCreatingAssignment() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

        service.assign(teacherId, classId, role, assignedDate);

        verify(repository).existsByTeacherIdAndClassId(teacherId, classId);
    }

    @Test
    @DisplayName("Should save assignment after creation")
    void shouldSaveAssignmentAfterCreation() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

        service.assign(teacherId, classId, role, assignedDate);

        verify(repository).save(any(TeacherClassAssignment.class));
    }

    @Test
    @DisplayName("Should handle assignment with null date")
    void shouldHandleAssignmentWithNullDate() {
        when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

        TeacherClassAssignment result = service.assign(teacherId, classId, role, null);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.assignedDate());
    }

    @Test
    @DisplayName("Should handle all teacher roles")
    void shouldHandleAllTeacherRoles() {
        TeacherRole[] roles = {
            TeacherRole.homeroomTeacher(), TeacherRole.subjectTeacher(), TeacherRole.assistantTeacher()
        };

        for (TeacherRole testRole : roles) {
            when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

            TeacherClassAssignment result = service.assign(teacherId, classId, testRole, assignedDate);

            assertEquals(testRole, result.role());
        }
    }

    @Test
    @DisplayName("Should handle different assigned dates")
    void shouldHandleDifferentAssignedDates() {
        LocalDate[] dates = {LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 15), LocalDate.of(2024, 9, 1)};

        for (LocalDate date : dates) {
            when(repository.existsByTeacherIdAndClassId(teacherId, classId)).thenReturn(false);

            TeacherClassAssignment result = service.assign(teacherId, classId, role, date);

            assertEquals(date, result.assignedDate());
        }
    }

    @Test
    @DisplayName("Should handle assignments with different teachers")
    void shouldHandleAssignmentsWithDifferentTeachers() {
        UserId teacher1 = UserId.generate();
        UserId teacher2 = UserId.generate();

        when(repository.existsByTeacherIdAndClassId(teacher1, classId)).thenReturn(false);
        when(repository.existsByTeacherIdAndClassId(teacher2, classId)).thenReturn(false);

        TeacherClassAssignment assignment1 = service.assign(teacher1, classId, role, assignedDate);
        TeacherClassAssignment assignment2 = service.assign(teacher2, classId, role, assignedDate);

        assertEquals(teacher1, assignment1.teacherId());
        assertEquals(teacher2, assignment2.teacherId());
    }

    @Test
    @DisplayName("Should handle assignments with different classes")
    void shouldHandleAssignmentsWithDifferentClasses() {
        ClassId class1 = ClassId.generate();
        ClassId class2 = ClassId.generate();

        when(repository.existsByTeacherIdAndClassId(teacherId, class1)).thenReturn(false);
        when(repository.existsByTeacherIdAndClassId(teacherId, class2)).thenReturn(false);

        TeacherClassAssignment assignment1 = service.assign(teacherId, class1, role, assignedDate);
        TeacherClassAssignment assignment2 = service.assign(teacherId, class2, role, assignedDate);

        assertEquals(class1, assignment1.classId());
        assertEquals(class2, assignment2.classId());
    }
}
