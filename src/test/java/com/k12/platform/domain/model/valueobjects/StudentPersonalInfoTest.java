package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StudentPersonalInfo value object.
 * Target: 100% coverage
 */
@DisplayName("StudentPersonalInfo Value Object Tests")
class StudentPersonalInfoTest {

    @Test
    @DisplayName("Should create valid personal info")
    void shouldCreateValidPersonalInfo() {
        StudentPersonalInfo info = StudentPersonalInfo.of("John", "Doe", "2010-05-15");

        assertEquals("John", info.firstName());
        assertEquals("Doe", info.lastName());
        assertEquals("2010-05-15", info.dateOfBirth());
    }

    @Test
    @DisplayName("Should reject null first name")
    void shouldRejectNullFirstName() {
        assertThrows(IllegalArgumentException.class, () -> StudentPersonalInfo.of(null, "Doe", "2010-05-15"));
    }

    @Test
    @DisplayName("Should reject blank first name")
    void shouldRejectBlankFirstName() {
        assertThrows(IllegalArgumentException.class, () -> StudentPersonalInfo.of("   ", "Doe", "2010-05-15"));
    }

    @Test
    @DisplayName("Should reject null last name")
    void shouldRejectNullLastName() {
        assertThrows(IllegalArgumentException.class, () -> StudentPersonalInfo.of("John", null, "2010-05-15"));
    }

    @Test
    @DisplayName("Should reject blank last name")
    void shouldRejectBlankLastName() {
        assertThrows(IllegalArgumentException.class, () -> StudentPersonalInfo.of("John", "   ", "2010-05-15"));
    }

    @Test
    @DisplayName("Should accept null date of birth")
    void shouldAcceptNullDateOfBirth() {
        StudentPersonalInfo info = StudentPersonalInfo.of("John", "Doe", null);

        assertEquals("John", info.firstName());
        assertEquals("Doe", info.lastName());
        assertNull(info.dateOfBirth());
    }

    @Test
    @DisplayName("Should consider same info as equal")
    void shouldConsiderSameInfoAsEqual() {
        StudentPersonalInfo info1 = StudentPersonalInfo.of("John", "Doe", "2010-05-15");
        StudentPersonalInfo info2 = StudentPersonalInfo.of("John", "Doe", "2010-05-15");

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    @DisplayName("Should consider different info as not equal")
    void shouldConsiderDifferentInfoAsNotEqual() {
        StudentPersonalInfo info1 = StudentPersonalInfo.of("John", "Doe", "2010-05-15");
        StudentPersonalInfo info2 = StudentPersonalInfo.of("Jane", "Doe", "2010-05-15");

        assertNotEquals(info1, info2);
    }

    @Test
    @DisplayName("Should consider different dates as not equal")
    void shouldConsiderDifferentDatesAsNotEqual() {
        StudentPersonalInfo info1 = StudentPersonalInfo.of("John", "Doe", "2010-05-15");
        StudentPersonalInfo info2 = StudentPersonalInfo.of("John", "Doe", "2011-05-15");

        assertNotEquals(info1, info2);
    }

    @Test
    @DisplayName("Should handle unicode characters in names")
    void shouldHandleUnicodeCharacters() {
        StudentPersonalInfo info = StudentPersonalInfo.of("José", "García", "2010-05-15");

        assertEquals("José", info.firstName());
        assertEquals("García", info.lastName());
    }

    @Test
    @DisplayName("Should handle long names")
    void shouldHandleLongNames() {
        String longName = "A".repeat(100);
        StudentPersonalInfo info = StudentPersonalInfo.of(longName, longName, "2010-05-15");

        assertEquals(longName, info.firstName());
        assertEquals(longName, info.lastName());
    }
}
