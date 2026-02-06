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

        // JWT should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header, payload, and signature");
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

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header, payload, and signature");
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

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header, payload, and signature");
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
        assertThrows(Exception.class, () -> {
            tokenService.extractUserId(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for token without enough parts")
    void shouldThrowExceptionForTokenWithoutEnoughParts() {
        // Create a valid Base64 string but with only one part
        String singlePart =
                java.util.Base64.getUrlEncoder().withoutPadding().encodeToString("only-one-part".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.extractUserId(singlePart);
        });
    }

    @Test
    @DisplayName("Should generate token with valid JWT structure")
    void shouldGenerateTokenWithValidJWTStructure() {
        String token = tokenService.generateToken(testUser);

        // JWT should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header, payload, and signature");

        // Each part should be valid Base64 URL encoding
        for (String part : parts) {
            assertDoesNotThrow(
                    () -> {
                        java.util.Base64.getUrlDecoder().decode(part);
                    },
                    "Each JWT part should be valid Base64 URL encoding");
        }
    }

    @Test
    @DisplayName("Should generate token with user ID in payload")
    void shouldGenerateTokenWithUserIdInPayload() {
        String token = tokenService.generateToken(testUser);

        // Extract payload from JWT
        String[] parts = token.split("\\.");
        String payload =
                new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);

        // Payload should contain subject (user ID)
        assertTrue(payload.contains("\"sub\""), "Payload should contain subject claim");
        assertTrue(payload.contains(testUser.userId().toString()), "Payload should contain user ID");
    }
}
