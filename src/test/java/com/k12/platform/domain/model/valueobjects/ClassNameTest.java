package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ClassName value object.
 * Target: 100% coverage
 */
@DisplayName("ClassName Value Object Tests")
class ClassNameTest {

    @Test
    @DisplayName("Should create valid class name")
    void shouldCreateValidClassName() {
        ClassName name = ClassName.of("Grade 5-A");
        assertEquals("Grade 5-A", name.value());
    }

    @Test
    @DisplayName("Should reject null class name")
    void shouldRejectNullClassName() {
        assertThrows(IllegalArgumentException.class, () -> ClassName.of(null));
    }

    @Test
    @DisplayName("Should reject blank class name")
    void shouldRejectBlankClassName() {
        assertThrows(IllegalArgumentException.class, () -> ClassName.of("   "));
    }

    @Test
    @DisplayName("Should reject class name over 100 characters")
    void shouldRejectClassNameOver100Characters() {
        String tooLong = "A".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> ClassName.of(tooLong));
    }

    @Test
    @DisplayName("Should trim whitespace")
    void shouldTrimWhitespace() {
        ClassName name = ClassName.of("  Grade 5-A  ");
        assertEquals("Grade 5-A", name.value());
    }

    @Test
    @DisplayName("Should consider same names as equal")
    void shouldConsiderSameNamesAsEqual() {
        ClassName name1 = ClassName.of("Grade 5-A");
        ClassName name2 = ClassName.of("Grade 5-A");

        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    @DisplayName("Should consider different names as not equal")
    void shouldConsiderDifferentNamesAsNotEqual() {
        ClassName name1 = ClassName.of("Grade 5-A");
        ClassName name2 = ClassName.of("Grade 5-B");

        assertNotEquals(name1, name2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        ClassName name = ClassName.of("Room 101");
        assertEquals("Room 101", name.toString());
    }

    @Test
    @DisplayName("Should accept exactly 100 characters")
    void shouldAcceptExactly100Characters() {
        String exactly100 = "A".repeat(100);
        ClassName name = ClassName.of(exactly100);
        assertEquals(exactly100, name.value());
    }
}
