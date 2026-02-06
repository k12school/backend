package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StudentId value object.
 * Target: 100% coverage
 */
@DisplayName("StudentId Value Object Tests")
class StudentIdTest {

    @Test
    @DisplayName("Should create from UUID")
    void shouldCreateFromUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        StudentId studentId = StudentId.of(uuid);

        assertEquals(uuid, studentId.value());
    }

    @Test
    @DisplayName("Should create from UUID string")
    void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        StudentId studentId = StudentId.of(uuidString);

        assertEquals(java.util.UUID.fromString(uuidString), studentId.value());
    }

    @Test
    @DisplayName("Should generate random StudentId")
    void shouldGenerateRandomStudentId() {
        StudentId studentId1 = StudentId.generate();
        StudentId studentId2 = StudentId.generate();

        assertNotNull(studentId1.value());
        assertNotNull(studentId2.value());
        assertNotEquals(studentId1, studentId2);
    }

    @Test
    @DisplayName("Should reject null UUID")
    void shouldRejectNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> StudentId.of((java.util.UUID) null));
    }

    @Test
    @DisplayName("Should reject invalid UUID string")
    void shouldRejectInvalidUUIDString() {
        assertThrows(IllegalArgumentException.class, () -> StudentId.of("not-a-uuid"));
    }

    @Test
    @DisplayName("Should consider same IDs as equal")
    void shouldConsiderSameIdsAsEqual() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        StudentId id1 = StudentId.of(uuid);
        StudentId id2 = StudentId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should consider different IDs as not equal")
    void shouldConsiderDifferentIdsAsNotEqual() {
        StudentId id1 = StudentId.generate();
        StudentId id2 = StudentId.generate();

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToString() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        StudentId studentId = StudentId.of(uuid);

        assertEquals(uuid.toString(), studentId.toString());
    }
}
