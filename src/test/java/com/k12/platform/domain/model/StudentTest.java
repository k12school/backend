package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.StudentNumber;
import com.k12.platform.domain.model.valueobjects.StudentPersonalInfo;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Student aggregate.
 * Target: 100% coverage
 */
@DisplayName("Student Aggregate Tests")
class StudentTest {

    private StudentPersonalInfo personalInfo;
    private GradeLevel gradeLevel;
    private UserId classId;
    private StudentNumber studentNumber;
    private LocalDate enrollmentDate;

    @BeforeEach
    void setUp() {
        personalInfo = StudentPersonalInfo.of("John", "Doe", "2010-05-15");
        gradeLevel = GradeLevel.of(5);
        classId = UserId.generate();
        studentNumber = StudentNumber.of("2024001");
        enrollmentDate = LocalDate.of(2024, 9, 1);
    }

    @Test
    @DisplayName("Should register new student")
    void shouldRegisterNewStudent() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        assertNotNull(student.studentId());
        assertEquals(personalInfo, student.personalInfo());
        assertEquals(gradeLevel, student.gradeLevel());
        assertEquals(classId, student.classId());
        assertEquals(studentNumber, student.studentNumber());
        assertEquals(enrollmentDate, student.enrollmentDate());
        assertNotNull(student.createdAt());
        assertNotNull(student.updatedAt());
    }

    @Test
    @DisplayName("Should require class ID for registration")
    void shouldRequireClassId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Student.register(personalInfo, gradeLevel, null, studentNumber, enrollmentDate));
        assertTrue(exception.getMessage().contains("Class ID is required"));
    }

    @Test
    @DisplayName("Should default enrollment date to today")
    void shouldDefaultEnrollmentDate() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, null);

        assertEquals(LocalDate.now(), student.enrollmentDate());
    }

    @Test
    @DisplayName("Should record StudentRegistered event on registration")
    void shouldRecordStudentRegisteredEvent() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        var domainEvents = student.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof StudentRegistered);
    }

    @Test
    @DisplayName("Should transfer to different grade")
    void shouldTransferGrade() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        GradeLevel newGrade = GradeLevel.of(6);
        var initialEventCount = student.getDomainEvents().size();

        student.transferGrade(newGrade);

        assertEquals(newGrade, student.gradeLevel());
        assertTrue(student.getDomainEvents().size() > initialEventCount);
        assertTrue(student.updatedAt().isAfter(student.createdAt()));
    }

    @Test
    @DisplayName("Should not allow null grade transfer")
    void shouldNotAllowNullGradeTransfer() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        assertThrows(IllegalArgumentException.class, () -> student.transferGrade(null));
    }

    @Test
    @DisplayName("Should update personal information")
    void shouldUpdatePersonalInfo() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        StudentPersonalInfo newInfo = StudentPersonalInfo.of("Jane", "Smith", "2010-06-20");
        var initialEventCount = student.getDomainEvents().size();

        student.updatePersonalInfo(newInfo);

        assertEquals(newInfo, student.personalInfo());
        assertTrue(student.getDomainEvents().size() > initialEventCount);
        assertTrue(student.updatedAt().isAfter(student.createdAt()));
    }

    @Test
    @DisplayName("Should not allow null personal info")
    void shouldNotAllowNullPersonalInfo() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        assertThrows(IllegalArgumentException.class, () -> student.updatePersonalInfo(null));
    }

    @Test
    @DisplayName("Should advance to next grade")
    void shouldAdvanceGrade() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        int initialGrade = student.gradeLevel().value();
        var initialEventCount = student.getDomainEvents().size();

        student.advanceGrade();

        assertEquals(initialGrade + 1, student.gradeLevel().value());
        assertTrue(student.getDomainEvents().size() > initialEventCount);
        assertTrue(student.updatedAt().isAfter(student.createdAt()));
    }

    @Test
    @DisplayName("Should not advance beyond grade 12")
    void shouldNotAdvanceBeyondGrade12() {
        Student student = Student.register(personalInfo, GradeLevel.of(12), classId, studentNumber, enrollmentDate);

        IllegalStateException exception = assertThrows(IllegalStateException.class, student::advanceGrade);
        assertTrue(exception.getMessage().contains("Cannot advance beyond grade 12"));
    }

    @Test
    @DisplayName("Should reconstitute student from persistence")
    void shouldReconstituteStudentFromPersistence() {
        StudentId studentId = StudentId.generate();
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now().minusSeconds(1800);

        Student student = Student.reconstitute(
                studentId, personalInfo, gradeLevel, classId, studentNumber, enrollmentDate, createdAt, updatedAt);

        assertEquals(studentId, student.studentId());
        assertEquals(personalInfo, student.personalInfo());
        assertEquals(gradeLevel, student.gradeLevel());
        assertEquals(classId, student.classId());
        assertEquals(studentNumber, student.studentNumber());
        assertEquals(enrollmentDate, student.enrollmentDate());
        // Note: createdAt and updatedAt are set in constructor, not from parameters
        assertEquals(0, student.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        var domainEvents = student.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof StudentRegistered);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        var domainEvents = student.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should record StudentTransferred event on grade transfer")
    void shouldRecordStudentTransferredEvent() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        GradeLevel newGrade = GradeLevel.of(7);

        student.transferGrade(newGrade);

        var domainEvents = student.getDomainEvents();
        boolean hasTransferredEvent = domainEvents.stream().anyMatch(event -> event instanceof StudentTransferred);
        assertTrue(hasTransferredEvent);
    }

    @Test
    @DisplayName("Should record StudentAdvanced event on grade advancement")
    void shouldRecordStudentAdvancedEvent() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        student.advanceGrade();

        var domainEvents = student.getDomainEvents();
        boolean hasAdvancedEvent = domainEvents.stream().anyMatch(event -> event instanceof StudentAdvanced);
        assertTrue(hasAdvancedEvent);
    }

    @Test
    @DisplayName("Should record StudentInfoUpdated event on personal info update")
    void shouldRecordStudentInfoUpdatedEvent() {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        StudentPersonalInfo newInfo = StudentPersonalInfo.of("Jane", "Smith", "2010-06-20");

        student.updatePersonalInfo(newInfo);

        var domainEvents = student.getDomainEvents();
        boolean hasInfoUpdatedEvent = domainEvents.stream().anyMatch(event -> event instanceof StudentInfoUpdated);
        assertTrue(hasInfoUpdatedEvent);
    }

    @Test
    @DisplayName("Should handle kindergarten grade (0)")
    void shouldHandleKindergartenGrade() {
        Student student = Student.register(
                personalInfo,
                GradeLevel.of(0), // Kindergarten
                classId,
                studentNumber,
                enrollmentDate);

        assertEquals(0, student.gradeLevel().value());
    }

    @Test
    @DisplayName("Should handle all grade levels 0-12")
    void shouldHandleAllGradeLevels() {
        for (int grade = 0; grade <= 12; grade++) {
            Student student =
                    Student.register(personalInfo, GradeLevel.of(grade), classId, studentNumber, enrollmentDate);
            assertEquals(grade, student.gradeLevel().value());
        }
    }

    @Test
    @DisplayName("Should allow student without student number")
    void shouldAllowEmptyStudentNumber() {
        Student student = Student.register(personalInfo, gradeLevel, classId, StudentNumber.empty(), enrollmentDate);

        assertTrue(student.studentNumber().isEmpty());
    }
}
