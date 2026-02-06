package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResetTokenId value object.
 * Target: 100% coverage
 */
@DisplayName("ResetTokenId Value Object Tests")
class ResetTokenIdTest {

    @Test
    @DisplayName("Should create from UUID")
    void shouldCreateFromUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ResetTokenId tokenId = ResetTokenId.of(uuid);

        assertEquals(uuid, tokenId.value());
    }

    @Test
    @DisplayName("Should create from UUID string")
    void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        ResetTokenId tokenId = ResetTokenId.of(uuidString);

        assertEquals(java.util.UUID.fromString(uuidString), tokenId.value());
    }

    @Test
    @DisplayName("Should generate random ResetTokenId")
    void shouldGenerateRandomResetTokenId() {
        ResetTokenId id1 = ResetTokenId.generate();
        ResetTokenId id2 = ResetTokenId.generate();

        assertNotNull(id1.value());
        assertNotNull(id2.value());
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should reject null UUID")
    void shouldRejectNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> ResetTokenId.of((java.util.UUID) null));
    }

    @Test
    @DisplayName("Should reject invalid UUID string")
    void shouldRejectInvalidUUIDString() {
        assertThrows(IllegalArgumentException.class, () -> ResetTokenId.of("not-a-uuid"));
    }

    @Test
    @DisplayName("Should consider same IDs as equal")
    void shouldConsiderSameIdsAsEqual() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ResetTokenId id1 = ResetTokenId.of(uuid);
        ResetTokenId id2 = ResetTokenId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should consider different IDs as not equal")
    void shouldConsiderDifferentIdsAsNotEqual() {
        ResetTokenId id1 = ResetTokenId.generate();
        ResetTokenId id2 = ResetTokenId.generate();

        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ResetTokenId tokenId = ResetTokenId.of(uuid);

        assertEquals(uuid.toString(), tokenId.toString());
    }
}
