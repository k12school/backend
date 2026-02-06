package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AssociationId value object.
 * Target: 100% coverage
 */
@DisplayName("AssociationId Value Object Tests")
class AssociationIdTest {

    @Test
    @DisplayName("Should create from UUID")
    void shouldCreateFromUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        AssociationId associationId = AssociationId.of(uuid);

        assertEquals(uuid, associationId.value());
    }

    @Test
    @DisplayName("Should create from UUID string")
    void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        AssociationId associationId = AssociationId.of(uuidString);

        assertEquals(java.util.UUID.fromString(uuidString), associationId.value());
    }

    @Test
    @DisplayName("Should generate random AssociationId")
    void shouldGenerateRandomAssociationId() {
        AssociationId id1 = AssociationId.generate();
        AssociationId id2 = AssociationId.generate();

        assertNotNull(id1.value());
        assertNotNull(id2.value());
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should reject null UUID")
    void shouldRejectNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> AssociationId.of((java.util.UUID) null));
    }

    @Test
    @DisplayName("Should reject invalid UUID string")
    void shouldRejectInvalidUUIDString() {
        assertThrows(IllegalArgumentException.class, () -> AssociationId.of("not-a-uuid"));
    }

    @Test
    @DisplayName("Should consider same IDs as equal")
    void shouldConsiderSameIdsAsEqual() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        AssociationId id1 = AssociationId.of(uuid);
        AssociationId id2 = AssociationId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should consider different IDs as not equal")
    void shouldConsiderDifferentIdsAsNotEqual() {
        AssociationId id1 = AssociationId.generate();
        AssociationId id2 = AssociationId.generate();

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        AssociationId associationId = AssociationId.of(uuid);

        assertEquals(uuid.toString(), associationId.toString());
    }
}
