package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.AcademicYear;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.ClassName;
import com.k12.platform.domain.model.valueobjects.GradeLevel;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Class aggregate.
 * Target: 100% coverage
 */
@DisplayName("Class Aggregate Tests")
class ClassTest {

    @Test
    @DisplayName("Should create new class")
    void shouldCreateNewClass() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Class clazz = Class.create(name, gradeLevel, academicYear);

        assertNotNull(clazz.getClassId());
        assertEquals(name, clazz.getName());
        assertEquals(gradeLevel, clazz.getGradeLevel());
        assertEquals(academicYear, clazz.getAcademicYear());
        assertNotNull(clazz.getCreatedAt());
        assertNotNull(clazz.getUpdatedAt());
    }

    @Test
    @DisplayName("Should record ClassCreated event on creation")
    void shouldRecordClassCreatedEvent() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Class clazz = Class.create(name, gradeLevel, academicYear);

        var domainEvents = clazz.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof ClassCreated);
    }

    @Test
    @DisplayName("Should reconstitute class from persistence")
    void shouldReconstituteClassFromPersistence() {
        ClassId classId = ClassId.generate();
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now().minusSeconds(1800);

        Class clazz = Class.reconstitute(classId, name, gradeLevel, academicYear, createdAt, updatedAt);

        assertEquals(classId, clazz.getClassId());
        assertEquals(name, clazz.getName());
        assertEquals(gradeLevel, clazz.getGradeLevel());
        assertEquals(academicYear, clazz.getAcademicYear());
        assertEquals(0, clazz.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Class clazz = Class.create(name, gradeLevel, academicYear);

        var domainEvents = clazz.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof ClassCreated);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Class clazz = Class.create(name, gradeLevel, academicYear);
        var domainEvents = clazz.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should handle different grade levels")
    void shouldHandleDifferentGradeLevels() {
        ClassName name = ClassName.of("K-A");
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        for (int grade = 0; grade <= 12; grade++) {
            GradeLevel gradeLevel = GradeLevel.of(grade);
            Class clazz = Class.create(name, gradeLevel, academicYear);
            assertEquals(gradeLevel, clazz.getGradeLevel());
        }
    }

    @Test
    @DisplayName("Should handle different academic years")
    void shouldHandleDifferentGetAcademicYears() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);

        AcademicYear year1 = AcademicYear.of("2024-2025");
        AcademicYear year2 = AcademicYear.of("2025-2026");
        AcademicYear year3 = AcademicYear.of("2026-2027");

        Class class1 = Class.create(name, gradeLevel, year1);
        Class class2 = Class.create(name, gradeLevel, year2);
        Class class3 = Class.create(name, gradeLevel, year3);

        assertEquals(year1, class1.getAcademicYear());
        assertEquals(year2, class2.getAcademicYear());
        assertEquals(year3, class3.getAcademicYear());
    }

    @Test
    @DisplayName("Should handle different class names")
    void shouldHandleDifferentClassNames() {
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        ClassName name1 = ClassName.of("5-A");
        ClassName name2 = ClassName.of("5-B");
        ClassName name3 = ClassName.of("Room 101");

        Class class1 = Class.create(name1, gradeLevel, academicYear);
        Class class2 = Class.create(name2, gradeLevel, academicYear);
        Class class3 = Class.create(name3, gradeLevel, academicYear);

        assertEquals(name1, class1.getName());
        assertEquals(name2, class2.getName());
        assertEquals(name3, class3.getName());
    }

    @Test
    @DisplayName("Should have unique class IDs")
    void shouldHaveUniqueGetClassIds() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Class class1 = Class.create(name, gradeLevel, academicYear);
        Class class2 = Class.create(name, gradeLevel, academicYear);

        assertNotEquals(class1.getClassId(), class2.getClassId());
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt on creation")
    void shouldSetTimestampsOnCreation() {
        ClassName name = ClassName.of("5-A");
        GradeLevel gradeLevel = GradeLevel.of(5);
        AcademicYear academicYear = AcademicYear.of("2024-2025");

        Instant beforeCreation = Instant.now();
        Class clazz = Class.create(name, gradeLevel, academicYear);
        Instant afterCreation = Instant.now();

        assertNotNull(clazz.getCreatedAt());
        assertNotNull(clazz.getUpdatedAt());
        assertTrue(clazz.getCreatedAt().isAfter(beforeCreation.minusMillis(100)));
        assertTrue(clazz.getCreatedAt().isBefore(afterCreation.plusMillis(100)));
        // createdAt and updatedAt should be approximately equal
        assertTrue(Math.abs(clazz.getCreatedAt().toEpochMilli()
                        - clazz.getUpdatedAt().toEpochMilli())
                < 100);
    }
}
