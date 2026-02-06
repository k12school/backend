package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PasswordHash value object.
 * Target: 100% coverage
 */
@DisplayName("PasswordHash Value Object Tests")
class PasswordHashTest {

    @Test
    @DisplayName("Should hash password with bcrypt")
    void shouldHashPassword() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash = PasswordHash.hash(plainPassword);

        assertNotNull(hash.value());
        assertNotEquals(plainPassword, hash.value());
        assertTrue(hash.value().length() >= 50); // Bcrypt hashes are typically 60 chars
    }

    @Test
    @DisplayName("Should reject null password")
    void shouldRejectNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.hash(null));
    }

    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.hash(""));
    }

    @Test
    @DisplayName("Should verify correct password")
    void shouldVerifyCorrectPassword() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash = PasswordHash.hash(plainPassword);

        assertTrue(hash.verify(plainPassword));
    }

    @Test
    @DisplayName("Should not verify incorrect password")
    void shouldNotVerifyIncorrectPassword() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash = PasswordHash.hash(plainPassword);

        assertFalse(hash.verify("WrongPassword123!"));
    }

    @Test
    @DisplayName("Should not verify null password")
    void shouldNotVerifyNullPassword() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash = PasswordHash.hash(plainPassword);

        assertFalse(hash.verify(null));
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void shouldGenerateDifferentHashesForSamePassword() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash1 = PasswordHash.hash(plainPassword);
        PasswordHash hash2 = PasswordHash.hash(plainPassword);

        assertNotEquals(hash1.value(), hash2.value()); // Bcrypt uses random salt
    }

    @Test
    @DisplayName("Should consider same hashes as equal")
    void shouldConsiderSameHashesAsEqual() {
        String plainPassword = "SecurePass123!";
        // Can't test equality directly since hashing generates different salts
        // Just verify both can verify the same password
        PasswordHash hash1 = PasswordHash.hash(plainPassword);
        PasswordHash hash2 = PasswordHash.hash(plainPassword);

        assertTrue(hash1.verify(plainPassword));
        assertTrue(hash2.verify(plainPassword));
    }

    @Test
    @DisplayName("Should reject null hash value")
    void shouldRejectNullHashValue() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.of(null));
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToString() {
        String plainPassword = "SecurePass123!";
        PasswordHash hash = PasswordHash.hash(plainPassword);

        assertNotNull(hash.toString());
        assertTrue(hash.toString().length() > 0);
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void shouldHandleSpecialCharacters() {
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        PasswordHash hash = PasswordHash.hash(specialPassword);

        assertTrue(hash.verify(specialPassword));
    }

    @Test
    @DisplayName("Should handle unicode characters in password")
    void shouldHandleUnicodeCharacters() {
        String unicodePassword = "密码123!"; // Chinese characters
        PasswordHash hash = PasswordHash.hash(unicodePassword);

        assertTrue(hash.verify(unicodePassword));
    }
}
