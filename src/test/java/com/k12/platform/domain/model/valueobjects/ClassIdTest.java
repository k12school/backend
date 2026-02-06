package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ClassId value object.
 * Target: 100% coverage
 */
@DisplayName("ClassId Value Object Tests")
class ClassIdTest {

    @Test
    @DisplayName("Should create from UUID")
    void shouldCreateFromUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ClassId classId = ClassId.of(uuid);

        assertEquals(uuid, classId.value());
    }

    @Test
    @DisplayName("Should create from UUID string")
    void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        ClassId classId = ClassId.of(uuidString);

        assertEquals(java.util.UUID.fromString(uuidString), classId.value());
    }

    @Test
    @DisplayName("Should generate random ClassId")
    void shouldGenerateRandomClassId() {
        ClassId classId1 = ClassId.generate();
        ClassId classId2 = ClassId.generate();

        assertNotNull(classId1.value());
        assertNotNull(classId2.value());
        assertNotEquals(classId1, classId2);
    }

    @Test
    @DisplayName("Should reject null UUID")
    void shouldRejectNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> ClassId.of((java.util.UUID) null));
    }

    @Test
    @DisplayName("Should reject invalid UUID string")
    void shouldRejectInvalidUUIDString() {
        assertThrows(IllegalArgumentException.class, () -> ClassId.of("not-a-uuid"));
    }

    @Test
    @DisplayName("Should consider same IDs as equal")
    void shouldConsiderSameIdsAsEqual() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ClassId id1 = ClassId.of(uuid);
        ClassId id2 = ClassId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should consider different IDs as not equal")
    void shouldConsiderDifferentIdsAsNotEqual() {
        ClassId id1 = ClassId.generate();
        ClassId id2 = ClassId.generate();

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToString() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ClassId classId = ClassId.of(uuid);

        assertEquals(uuid.toString(), classId.toString());
    }
}
