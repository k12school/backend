package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.exceptions.InvalidGradeLevelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for GradeLevel value object.
 * Target: 100% coverage
 */
@DisplayName("GradeLevel Value Object Tests")
class GradeLevelTest {

    @Test
    @DisplayName("Should create grade level from integer")
    void shouldCreateGradeLevelFromInteger() {
        GradeLevel grade = GradeLevel.of(5);
        assertEquals(5, grade.value());
        assertEquals("5", grade.displayValue());
    }

    @Test
    @DisplayName("Should create grade K from integer 0")
    void shouldCreateGradeKFromInteger() {
        GradeLevel grade = GradeLevel.of(0);
        assertEquals(0, grade.value());
        assertEquals("K", grade.displayValue());
    }

    @Test
    @DisplayName("Should create grade from string K")
    void shouldCreateGradeFromStringK() {
        GradeLevel grade = GradeLevel.fromString("K");
        assertEquals(0, grade.value());
        assertEquals("K", grade.displayValue());
    }

    @Test
    @DisplayName("Should create grade from string 1-12")
    void shouldCreateGradeFromStringOneToTwelve() {
        GradeLevel grade1 = GradeLevel.fromString("1");
        assertEquals(1, grade1.value());

        GradeLevel grade12 = GradeLevel.fromString("12");
        assertEquals(12, grade12.value());
    }

    @Test
    @DisplayName("Should create grade from string 0")
    void shouldCreateGradeFromStringZero() {
        GradeLevel grade = GradeLevel.fromString("0");
        assertEquals(0, grade.value());
        assertEquals("K", grade.displayValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 12})
    @DisplayName("Should accept valid grade levels")
    void shouldAcceptValidGradeLevels(int grade) {
        assertDoesNotThrow(() -> GradeLevel.of(grade));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 13, 100})
    @DisplayName("Should reject invalid grade levels")
    void shouldRejectInvalidGradeLevels(int grade) {
        assertThrows(InvalidGradeLevelException.class, () -> GradeLevel.of(grade));
    }

    @Test
    @DisplayName("Should reject invalid string grade levels")
    void shouldRejectInvalidStringGradeLevels() {
        assertThrows(InvalidGradeLevelException.class, () -> GradeLevel.fromString("13"));
        assertThrows(InvalidGradeLevelException.class, () -> GradeLevel.fromString("-1"));
        assertThrows(InvalidGradeLevelException.class, () -> GradeLevel.fromString("ABC"));
    }

    @Test
    @DisplayName("Should consider same grades as equal")
    void shouldConsiderSameGradesAsEqual() {
        GradeLevel grade1 = GradeLevel.of(5);
        GradeLevel grade2 = GradeLevel.of(5);

        assertEquals(grade1, grade2);
        assertEquals(grade1.hashCode(), grade2.hashCode());
    }

    @Test
    @DisplayName("Should consider different grades as not equal")
    void shouldConsiderDifferentGradesAsNotEqual() {
        GradeLevel grade1 = GradeLevel.of(1);
        GradeLevel grade2 = GradeLevel.of(2);

        assertNotEquals(grade1, grade2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        assertEquals("K", GradeLevel.of(0).toString());
        assertEquals("1", GradeLevel.of(1).toString());
        assertEquals("12", GradeLevel.of(12).toString());
    }
}
