package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.StudentNumber;
import com.k12.platform.domain.model.valueobjects.StudentPersonalInfo;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.LocalDate;

import com.k12.platform.domain.port.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for StudentRegistrationService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("StudentRegistrationService Tests")
class StudentRegistrationServiceTest {

    @Mock
    StudentRepository studentRepository;

    StudentRegistrationService studentRegistrationService;

    private StudentPersonalInfo personalInfo;
    private GradeLevel gradeLevel;
    private UserId classId;
    private StudentNumber studentNumber;
    private LocalDate enrollmentDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentRegistrationService = new StudentRegistrationService(studentRepository);
        personalInfo = StudentPersonalInfo.of("John", "Doe", "2010-05-15");
        gradeLevel = GradeLevel.of(5);
        classId = UserId.generate();
        studentNumber = StudentNumber.of("2024001");
        enrollmentDate = LocalDate.of(2024, 9, 1);
        doNothing().when(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should register new student successfully")
    void shouldRegisterNewStudentSuccessfully() {
        Student result = studentRegistrationService.registerStudent(
                personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        assertNotNull(result);
        assertEquals(personalInfo, result.personalInfo());
        assertEquals(gradeLevel, result.gradeLevel());
        assertEquals(classId, result.classId());
        assertEquals(studentNumber, result.studentNumber());
        assertEquals(enrollmentDate, result.enrollmentDate());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should register student without student number")
    void shouldRegisterStudentWithoutStudentNumber() {
        Student result = studentRegistrationService.registerStudent(
                personalInfo, gradeLevel, classId, StudentNumber.empty(), enrollmentDate);

        assertNotNull(result);
        assertTrue(result.studentNumber().isEmpty());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should register student with default enrollment date")
    void shouldRegisterStudentWithDefaultEnrollmentDate() {
        Student result =
                studentRegistrationService.registerStudent(personalInfo, gradeLevel, classId, studentNumber, null);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.enrollmentDate());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should register student in kindergarten")
    void shouldRegisterStudentInKindergarten() {
        Student result = studentRegistrationService.registerStudent(
                personalInfo,
                GradeLevel.of(0), // Kindergarten
                classId,
                studentNumber,
                enrollmentDate);

        assertNotNull(result);
        assertEquals(0, result.gradeLevel().value());
    }

    @Test
    @DisplayName("Should save student after registration")
    void shouldSaveStudentAfterRegistration() {
        studentRegistrationService.registerStudent(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should reconstitute student from persistence")
    void shouldReconstituteStudentFromPersistence() {
        StudentId studentId = StudentId.generate();

        Student result = studentRegistrationService.reconstituteStudent(
                studentId, personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        assertNotNull(result);
        assertEquals(studentId, result.studentId());
        assertEquals(personalInfo, result.personalInfo());
        assertEquals(gradeLevel, result.gradeLevel());
    }

    @Test
    @DisplayName("Should not save when reconstituting")
    void shouldNotSaveWhenReconstituting() {
        StudentId studentId = StudentId.generate();

        studentRegistrationService.reconstituteStudent(
                studentId, personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should handle all grade levels")
    void shouldHandleAllGradeLevels() {
        for (int grade = 0; grade <= 12; grade++) {
            Student result = studentRegistrationService.registerStudent(
                    personalInfo, GradeLevel.of(grade), classId, studentNumber, enrollmentDate);

            assertEquals(grade, result.gradeLevel().value());
        }
    }
}
