package com.k12.platform.interfaces.rest.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.model.valueobjects.UserId;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TokenService.
 * Target: 90%+ coverage
 */
@QuarkusTest
@DisplayName("TokenService Tests")
class TokenServiceTest {

    @Inject
    TokenService tokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.register(
                EmailAddress.of("test@example.com"), PasswordHash.hash("password123"), "John", "Doe", UserRole.TEACHER);
    }

    @Test
    @DisplayName("Should generate token for teacher")
    void shouldGenerateTokenForTeacher() {
        String token = tokenService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should generate token for parent")
    void shouldGenerateTokenForParent() {
        User parent = User.register(
                EmailAddress.of("parent@example.com"),
                PasswordHash.hash("password123"),
                "Jane",
                "Smith",
                UserRole.PARENT);

        String token = tokenService.generateToken(parent);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should generate token for admin")
    void shouldGenerateTokenForAdmin() {
        User admin = User.register(
                EmailAddress.of("admin@example.com"),
                PasswordHash.hash("password123"),
                "Admin",
                "User",
                UserRole.ADMIN);

        String token = tokenService.generateToken(admin);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        User user1 = User.register(
                EmailAddress.of("user1@example.com"),
                PasswordHash.hash("password123"),
                "User",
                "One",
                UserRole.TEACHER);

        User user2 = User.register(
                EmailAddress.of("user2@example.com"),
                PasswordHash.hash("password123"),
                "User",
                "Two",
                UserRole.TEACHER);

        String token1 = tokenService.generateToken(user1);
        String token2 = tokenService.generateToken(user2);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should generate different tokens for same user at different times")
    void shouldGenerateDifferentTokensForSameUserAtDifferentTimes() throws InterruptedException {
        String token1 = tokenService.generateToken(testUser);

        Thread.sleep(10); // Small delay to ensure different timestamp

        String token2 = tokenService.generateToken(testUser);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void shouldExtractUserIdFromToken() {
        String token = tokenService.generateToken(testUser);

        UserId extractedUserId = tokenService.extractUserId(token);

        assertEquals(testUser.userId(), extractedUserId);
    }

    @Test
    @DisplayName("Should extract correct user ID for different users")
    void shouldExtractCorrectUserIdForDifferentUsers() {
        User user1 = User.register(
                EmailAddress.of("user1@example.com"),
                PasswordHash.hash("password123"),
                "User",
                "One",
                UserRole.TEACHER);

        String token = tokenService.generateToken(user1);

        UserId extractedUserId = tokenService.extractUserId(token);

        assertEquals(user1.userId(), extractedUserId);
    }

    @Test
    @DisplayName("Should extract user ID for parent")
    void shouldExtractUserIdForParent() {
        User parent = User.register(
                EmailAddress.of("parent@example.com"),
                PasswordHash.hash("password123"),
                "Jane",
                "Smith",
                UserRole.PARENT);

        String token = tokenService.generateToken(parent);

        UserId extractedUserId = tokenService.extractUserId(token);

        assertEquals(parent.userId(), extractedUserId);
    }

    @Test
    @DisplayName("Should extract user ID for admin")
    void shouldExtractUserIdForAdmin() {
        User admin = User.register(
                EmailAddress.of("admin@example.com"),
                PasswordHash.hash("password123"),
                "Admin",
                "User",
                UserRole.ADMIN);

        String token = tokenService.generateToken(admin);

        UserId extractedUserId = tokenService.extractUserId(token);

        assertEquals(admin.userId(), extractedUserId);
    }

    @Test
    @DisplayName("Should extract user ID from token multiple times")
    void shouldExtractUserIdFromTokenMultipleTimes() {
        String token = tokenService.generateToken(testUser);

        UserId extracted1 = tokenService.extractUserId(token);
        UserId extracted2 = tokenService.extractUserId(token);

        assertEquals(extracted1, extracted2);
        assertEquals(testUser.userId(), extracted1);
    }

    @Test
    @DisplayName("Should throw exception for invalid token format")
    void shouldThrowExceptionForInvalidTokenFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.extractUserId("invalid-token");
        });
    }

    @Test
    @DisplayName("Should throw exception for empty token")
    void shouldThrowExceptionForEmptyToken() {
        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.extractUserId("");
        });
    }

    @Test
    @DisplayName("Should throw exception for null token")
    void shouldThrowExceptionForNullToken() {
        assertThrows(NullPointerException.class, () -> {
            tokenService.extractUserId(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for malformed Base64")
    void shouldThrowExceptionForMalformedBase64() {
        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.extractUserId("not-valid-base64!@#$%");
        });
    }

    @Test
    @DisplayName("Should throw exception for token without enough parts")
    void shouldThrowExceptionForTokenWithoutEnoughParts() {
        // Create a valid Base64 string but with only one part
        String singlePart = java.util.Base64.getEncoder().encodeToString("only-one-part".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.extractUserId(singlePart);
        });
    }

    @Test
    @DisplayName("Should generate token with valid Base64 encoding")
    void shouldGenerateTokenWithValidBase64Encoding() {
        String token = tokenService.generateToken(testUser);

        // Should be valid Base64
        assertDoesNotThrow(() -> {
            java.util.Base64.getDecoder().decode(token);
        });
    }

    @Test
    @DisplayName("Should generate token with user ID and expiration")
    void shouldGenerateTokenWithUserIdAndExpiration() {
        String token = tokenService.generateToken(testUser);

        String decoded = new String(java.util.Base64.getDecoder().decode(token));
        String[] parts = decoded.split(":", 3);

        assertEquals(3, parts.length);
        assertEquals(testUser.userId().toString(), parts[0]);
        assertNotNull(parts[1]); // expiration timestamp
        assertEquals(testUser.email().value(), parts[2]);
    }
}
