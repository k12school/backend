package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.valueobjects.AcademicYear;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.ClassName;
import com.k12.platform.domain.model.valueobjects.GradeLevel;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for ClassService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("ClassService Tests")
class ClassServiceTest {

    @Mock
    ClassRepository classRepository;

    ClassService classService;

    private ClassName className;
    private GradeLevel gradeLevel;
    private AcademicYear academicYear;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        classService = new ClassService(classRepository);
        className = ClassName.of("5-A");
        gradeLevel = GradeLevel.of(5);
        academicYear = AcademicYear.of("2024-2025");
        doNothing().when(classRepository).save(any(Class.class));
    }

    @Test
    @DisplayName("Should create class successfully")
    void shouldCreateClassSuccessfully() {
        when(classRepository.existsByNameAndGradeLevelAndAcademicYear(
                        className.value(), gradeLevel.value(), academicYear.value()))
                .thenReturn(false);

        Class result = classService.createClass(className, gradeLevel, academicYear);

        assertNotNull(result);
        assertEquals(className, result.getName());
        assertEquals(gradeLevel, result.getGradeLevel());
        assertEquals(academicYear, result.getAcademicYear());
        verify(classRepository)
                .existsByNameAndGradeLevelAndAcademicYear(className.value(), gradeLevel.value(), academicYear.value());
        verify(classRepository).save(any(Class.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when class already exists")
    void shouldThrowIllegalStateExceptionWhenClassExists() {
        when(classRepository.existsByNameAndGradeLevelAndAcademicYear(
                        className.value(), gradeLevel.value(), academicYear.value()))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> classService.createClass(className, gradeLevel, academicYear));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(classRepository)
                .existsByNameAndGradeLevelAndAcademicYear(className.value(), gradeLevel.value(), academicYear.value());
        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    @DisplayName("Should not save class when it already exists")
    void shouldNotSaveClassWhenItExists() {
        when(classRepository.existsByNameAndGradeLevelAndAcademicYear(any(), anyInt(), any()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> classService.createClass(className, gradeLevel, academicYear));

        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    @DisplayName("Should reconstitute class from persistence")
    void shouldReconstituteClassFromPersistence() {
        ClassId classId = ClassId.generate();
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now().minusSeconds(1800);

        Class result =
                classService.reconstituteClass(classId, className, gradeLevel, academicYear, createdAt, updatedAt);

        assertNotNull(result);
        assertEquals(classId, result.getClassId());
        assertEquals(className, result.getName());
        assertEquals(gradeLevel, result.getGradeLevel());
        assertEquals(academicYear, result.getAcademicYear());
    }

    @Test
    @DisplayName("Should not save when reconstituting")
    void shouldNotSaveWhenReconstituting() {
        ClassId classId = ClassId.generate();

        classService.reconstituteClass(classId, className, gradeLevel, academicYear, Instant.now(), Instant.now());

        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    @DisplayName("Should check uniqueness before creating class")
    void shouldCheckUniquenessBeforeCreatingClass() {
        when(classRepository.existsByNameAndGradeLevelAndAcademicYear(
                        className.value(), gradeLevel.value(), academicYear.value()))
                .thenReturn(false);

        classService.createClass(className, gradeLevel, academicYear);

        verify(classRepository)
                .existsByNameAndGradeLevelAndAcademicYear(className.value(), gradeLevel.value(), academicYear.value());
    }

    @Test
    @DisplayName("Should save class after creation")
    void shouldSaveClassAfterCreation() {
        when(classRepository.existsByNameAndGradeLevelAndAcademicYear(any(), anyInt(), any()))
                .thenReturn(false);

        classService.createClass(className, gradeLevel, academicYear);

        verify(classRepository).save(any(Class.class));
    }
}
