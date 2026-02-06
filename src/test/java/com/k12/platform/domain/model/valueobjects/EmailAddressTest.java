package com.k12.platform.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.exceptions.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EmailAddress value object.
 * Target: 100% coverage
 */
@DisplayName("EmailAddress Value Object Tests")
class EmailAddressTest {

    @Test
    @DisplayName("Should create valid email address")
    void shouldCreateValidEmail() {
        EmailAddress email = EmailAddress.of("test@example.com");
        assertEquals("test@example.com", email.value());
    }

    @Test
    @DisplayName("Should reject null email")
    void shouldRejectNullEmail() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> EmailAddress.of(null));
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should reject blank email")
    void shouldRejectBlankEmail() {
        assertThrows(InvalidEmailException.class, () -> EmailAddress.of("   "));
    }

    @Test
    @DisplayName("Should reject invalid email format - no @")
    void shouldRejectInvalidEmailNoAt() {
        assertThrows(InvalidEmailException.class, () -> EmailAddress.of("invalidemail.com"));
    }

    @Test
    @DisplayName("Should reject invalid email format - no domain")
    void shouldRejectInvalidEmailNoDomain() {
        assertThrows(InvalidEmailException.class, () -> EmailAddress.of("test@"));
    }

    @Test
    @DisplayName("Should not trim whitespace")
    void shouldNotTrimWhitespace() {
        assertThrows(InvalidEmailException.class, () -> EmailAddress.of("  test@example.com  "));
    }

    @Test
    @DisplayName("Should consider same emails as equal")
    void shouldConsiderSameEmailsAsEqual() {
        EmailAddress email1 = EmailAddress.of("test@example.com");
        EmailAddress email2 = EmailAddress.of("test@example.com");
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("Should consider different emails as not equal")
    void shouldConsiderDifferentEmailsAsNotEqual() {
        EmailAddress email1 = EmailAddress.of("test1@example.com");
        EmailAddress email2 = EmailAddress.of("test2@example.com");
        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("Should convert to string correctly")
    void shouldConvertToString() {
        EmailAddress email = EmailAddress.of("test@example.com");
        assertEquals("test@example.com", email.toString());
    }

    @Test
    @DisplayName("Should accept email with subdomains")
    void shouldAcceptEmailWithSubdomains() {
        EmailAddress email = EmailAddress.of("user@mail.example.com");
        assertEquals("user@mail.example.com", email.value());
    }

    @Test
    @DisplayName("Should accept email with numbers")
    void shouldAcceptEmailWithNumbers() {
        EmailAddress email = EmailAddress.of("user123@example.com");
        assertEquals("user123@example.com", email.value());
    }

    @Test
    @DisplayName("Should accept email with dots in local part")
    void shouldAcceptEmailWithDots() {
        EmailAddress email = EmailAddress.of("first.last@example.com");
        assertEquals("first.last@example.com", email.value());
    }

    @Test
    @DisplayName("Should accept email with plus sign")
    void shouldAcceptEmailWithPlus() {
        EmailAddress email = EmailAddress.of("user+tag@example.com");
        assertEquals("user+tag@example.com", email.value());
    }
}
