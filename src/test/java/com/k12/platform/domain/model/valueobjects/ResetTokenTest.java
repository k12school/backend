package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResetToken value object.
 * Target: 100% coverage
 */
@DisplayName("ResetToken Value Object Tests")
class ResetTokenTest {

    @Test
    @DisplayName("Should create valid reset token")
    void shouldCreateValidResetToken() {
        ResetToken token = ResetToken.of("abc123-token");
        assertEquals("abc123-token", token.value());
    }

    @Test
    @DisplayName("Should generate random reset token")
    void shouldGenerateRandomResetToken() {
        ResetToken token1 = ResetToken.generate();
        ResetToken token2 = ResetToken.generate();

        assertNotNull(token1.value());
        assertNotNull(token2.value());
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should reject null token")
    void shouldRejectNullToken() {
        assertThrows(IllegalArgumentException.class, () -> ResetToken.of(null));
    }

    @Test
    @DisplayName("Should reject blank token")
    void shouldRejectBlankToken() {
        assertThrows(IllegalArgumentException.class, () -> ResetToken.of("   "));
    }

    @Test
    @DisplayName("Should consider same tokens as equal")
    void shouldConsiderSameTokensAsEqual() {
        ResetToken token1 = ResetToken.of("test-token");
        ResetToken token2 = ResetToken.of("test-token");

        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    @DisplayName("Should consider different tokens as not equal")
    void shouldConsiderDifferentTokensAsNotEqual() {
        ResetToken token1 = ResetToken.generate();
        ResetToken token2 = ResetToken.generate();

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToStringCorrectly() {
        ResetToken token = ResetToken.of("test-token");
        assertEquals("test-token", token.toString());
    }

    @Test
    @DisplayName("Generated token should be valid UUID")
    void generatedTokenShouldBeValidUUID() {
        ResetToken token = ResetToken.generate();
        // Should be a valid UUID format
        assertDoesNotThrow(() -> java.util.UUID.fromString(token.value()));
    }
}
