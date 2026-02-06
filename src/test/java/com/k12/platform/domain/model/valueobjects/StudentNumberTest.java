package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StudentNumber value object.
 * Target: 100% coverage
 */
@DisplayName("StudentNumber Value Object Tests")
class StudentNumberTest {

    @Test
    @DisplayName("Should create student number")
    void shouldCreateStudentNumber() {
        StudentNumber number = StudentNumber.of("STU001");

        assertEquals("STU001", number.value());
        assertFalse(number.isEmpty());
    }

    @Test
    @DisplayName("Should create empty student number")
    void shouldCreateEmptyStudentNumber() {
        StudentNumber number = StudentNumber.empty();

        assertNull(number.value());
        assertTrue(number.isEmpty());
    }

    @Test
    @DisplayName("Should reject blank student number")
    void shouldRejectBlankStudentNumber() {
        assertThrows(IllegalArgumentException.class, () -> StudentNumber.of("   "));
    }

    @Test
    @DisplayName("Should consider same numbers as equal")
    void shouldConsiderSameNumbersAsEqual() {
        StudentNumber number1 = StudentNumber.of("STU001");
        StudentNumber number2 = StudentNumber.of("STU001");

        assertEquals(number1, number2);
        assertEquals(number1.hashCode(), number2.hashCode());
    }

    @Test
    @DisplayName("Should consider different numbers as not equal")
    void shouldConsiderDifferentNumbersAsNotEqual() {
        StudentNumber number1 = StudentNumber.of("STU001");
        StudentNumber number2 = StudentNumber.of("STU002");

        assertNotEquals(number1, number2);
    }

    @Test
    @DisplayName("Should consider empty as equal to empty")
    void shouldConsiderEmptyAsEqualToEmpty() {
        StudentNumber number1 = StudentNumber.empty();
        StudentNumber number2 = StudentNumber.empty();

        // Both have null value, should be equal
        assertEquals(number1, number2);
    }

    @Test
    @DisplayName("Should not consider empty as equal to populated")
    void shouldNotConsiderEmptyAsEqualToPopulated() {
        StudentNumber empty = StudentNumber.empty();
        StudentNumber populated = StudentNumber.of("STU001");

        assertNotEquals(empty, populated);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        StudentNumber number = StudentNumber.of("STU001");
        assertEquals("STU001", number.toString());

        StudentNumber empty = StudentNumber.empty();
        assertEquals("[no student number]", empty.toString());
    }

    @Test
    @DisplayName("Should handle numeric student numbers")
    void shouldHandleNumericStudentNumbers() {
        StudentNumber number = StudentNumber.of("12345");
        assertEquals("12345", number.value());
    }

    @Test
    @DisplayName("Should handle alphanumeric student numbers")
    void shouldHandleAlphanumericStudentNumbers() {
        StudentNumber number = StudentNumber.of("ABC-123-XYZ");
        assertEquals("ABC-123-XYZ", number.value());
    }
}
