package com.k12.platform.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for PasswordValidator domain service.
 * Target: 100% coverage
 */
@DisplayName("PasswordValidator Service Tests")
class PasswordValidatorTest {

    @Test
    @DisplayName("Should validate valid password")
    void shouldValidateValidPassword() {
        assertDoesNotThrow(() -> PasswordValidator.validate("SecurePass123!"));
    }

    @Test
    @DisplayName("Should reject null password")
    void shouldRejectNullPassword() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate(null));
        assertTrue(exception.getMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Should reject blank password")
    void shouldRejectBlankPassword() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("   "));
        assertTrue(exception.getMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Should reject short password")
    void shouldRejectShortPassword() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("Short1!"));
        assertTrue(exception.getMessage().contains("at least 8 characters"));
    }

    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate(""));
        assertTrue(exception.getMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Should accept password with exactly 8 characters")
    void shouldAcceptPasswordWithExactly8Characters() {
        assertDoesNotThrow(() -> PasswordValidator.validate("12345678"));
    }

    @Test
    @DisplayName("Should accept password with more than 8 characters")
    void shouldAcceptLongerPassword() {
        assertDoesNotThrow(() -> PasswordValidator.validate("ThisIsAVeryLongPassword123!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password123!", "SecurePass456", "MyPassword@", "12345678", "abcdefgh", "!@#$%^&*"})
    @DisplayName("Should accept various valid passwords")
    void shouldAcceptVariousValidPasswords(String password) {
        assertDoesNotThrow(() -> PasswordValidator.validate(password));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject null and empty passwords")
    void shouldRejectNullAndEmptyPasswords(String password) {
        assertFalse(PasswordValidator.isValid(password));
    }

    @Test
    @DisplayName("Should return true for valid password")
    void shouldReturnTrueForValidPassword() {
        assertTrue(PasswordValidator.isValid("ValidPass123"));
    }

    @Test
    @DisplayName("Should return false for null password")
    void shouldReturnFalseForNullPassword() {
        assertFalse(PasswordValidator.isValid(null));
    }

    @Test
    @DisplayName("Should return false for short password")
    void shouldReturnFalseForShortPassword() {
        assertFalse(PasswordValidator.isValid("Short1"));
    }

    @Test
    @DisplayName("Should return false for blank password")
    void shouldReturnFalseForBlankPassword() {
        assertFalse(PasswordValidator.isValid("   "));
    }

    @Test
    @DisplayName("Should return false for empty password")
    void shouldReturnFalseForEmptyPassword() {
        assertFalse(PasswordValidator.isValid(""));
    }
}
