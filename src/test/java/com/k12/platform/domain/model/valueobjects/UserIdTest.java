package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for UserId value object.
 * Target: 100% coverage
 */
@DisplayName("UserId Value Object Tests")
class UserIdTest {

    @Test
    @DisplayName("Should create from UUID")
    void shouldCreateFromUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        assertEquals(uuid, userId.value());
    }

    @Test
    @DisplayName("Should create from UUID string")
    void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        UserId userId = UserId.of(uuidString);

        assertEquals(UUID.fromString(uuidString), userId.value());
    }

    @Test
    @DisplayName("Should generate random UserId")
    void shouldGenerateRandomUserId() {
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();

        assertNotNull(userId1.value());
        assertNotNull(userId2.value());
        assertNotEquals(userId1, userId2);
    }

    @Test
    @DisplayName("Should reject null UUID")
    void shouldRejectNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> UserId.of((UUID) null));
    }

    @Test
    @DisplayName("Should reject invalid UUID string")
    void shouldRejectInvalidUUIDString() {
        assertThrows(IllegalArgumentException.class, () -> UserId.of("not-a-uuid"));
    }

    @Test
    @DisplayName("Should consider same UUIDs as equal")
    void shouldConsiderSameUUIDsAsEqual() {
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.of(uuid);
        UserId userId2 = UserId.of(uuid);

        assertEquals(userId1, userId2);
        assertEquals(userId1.hashCode(), userId2.hashCode());
    }

    @Test
    @DisplayName("Should consider different UUIDs as not equal")
    void shouldConsiderDifferentUUIDsAsNotEqual() {
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();

        assertNotEquals(userId1, userId2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToString() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        assertEquals(uuid.toString(), userId.toString());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "00000000-0000-0000-0000-000000000000",
                "ffffffff-ffff-ffff-ffff-ffffffffffff",
                "123e4567-e89b-12d3-a456-426614174000"
            })
    @DisplayName("Should accept valid UUID formats")
    void shouldAcceptValidUUIDFormats(String uuidString) {
        UserId userId = UserId.of(uuidString);
        assertNotNull(userId.value());
    }
}
