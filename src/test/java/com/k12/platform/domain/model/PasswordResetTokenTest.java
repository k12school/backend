package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.ResetToken;
import com.k12.platform.domain.model.valueobjects.ResetTokenId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PasswordResetToken aggregate.
 * Target: 100% coverage
 */
@DisplayName("PasswordResetToken Aggregate Tests")
class PasswordResetTokenTest {

    @Test
    @DisplayName("Should create password reset token with default expiration")
    void shouldCreateTokenWithDefaultExpiration() {
        UserId userId = UserId.generate();

        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        assertNotNull(resetToken.tokenId());
        assertEquals(userId, resetToken.userId());
        assertNotNull(resetToken.token());
        assertNotNull(resetToken.expiresAt());
        assertNotNull(resetToken.createdAt());
        assertFalse(resetToken.isUsed());
        assertNull(resetToken.usedAt());
    }

    @Test
    @DisplayName("Should create password reset token with custom expiration")
    void shouldCreateTokenWithCustomExpiration() {
        UserId userId = UserId.generate();
        int hours = 48;

        PasswordResetToken resetToken = PasswordResetToken.create(userId, hours);

        assertNotNull(resetToken.tokenId());
        assertEquals(userId, resetToken.userId());
        assertNotNull(resetToken.expiresAt());
        long expectedExpirationSeconds = hours * 3600L;
        long actualExpirationSeconds =
                resetToken.expiresAt().getEpochSecond() - resetToken.createdAt().getEpochSecond();
        // Allow 1 second tolerance
        assertTrue(Math.abs(actualExpirationSeconds - expectedExpirationSeconds) <= 1);
    }

    @Test
    @DisplayName("Should record PasswordResetRequested event on creation")
    void shouldRecordPasswordResetRequestedEvent() {
        UserId userId = UserId.generate();

        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        var domainEvents = resetToken.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof PasswordResetRequested);
    }

    @Test
    @DisplayName("Should mark token as used")
    void shouldMarkTokenAsUsed() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        resetToken.markAsUsed();

        assertTrue(resetToken.isUsed());
        assertNotNull(resetToken.usedAt());
    }

    @Test
    @DisplayName("Should not allow marking already used token")
    void shouldNotAllowMarkingAlreadyUsedToken() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        resetToken.markAsUsed();

        assertThrows(IllegalStateException.class, resetToken::markAsUsed);
    }

    @Test
    @DisplayName("Should not allow marking expired token as used")
    void shouldNotAllowMarkingExpiredTokenAsUsed() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId, -1); // Already expired

        assertThrows(IllegalStateException.class, resetToken::markAsUsed);
    }

    @Test
    @DisplayName("Should check if token is expired")
    void shouldCheckIfTokenIsExpired() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        assertFalse(resetToken.isExpired());
    }

    @Test
    @DisplayName("Should be expired when expiration time passed")
    void shouldBeExpiredWhenExpirationTimePassed() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId, -1); // Expired

        assertTrue(resetToken.isExpired());
    }

    @Test
    @DisplayName("Should check if token is valid")
    void shouldCheckIfTokenIsValid() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        assertTrue(resetToken.isValid());
    }

    @Test
    @DisplayName("Should not be valid when used")
    void shouldNotBeValidWhenUsed() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        resetToken.markAsUsed();

        assertFalse(resetToken.isValid());
    }

    @Test
    @DisplayName("Should not be valid when expired")
    void shouldNotBeValidWhenExpired() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId, -1); // Expired

        assertFalse(resetToken.isValid());
    }

    @Test
    @DisplayName("Should reconstitute token from persistence")
    void shouldReconstituteTokenFromPersistence() {
        ResetTokenId tokenId = ResetTokenId.generate();
        UserId userId = UserId.generate();
        ResetToken token = ResetToken.generate();
        Instant expiresAt = Instant.now().plusSeconds(3600);
        Instant usedAt = Instant.now();
        Instant createdAt = Instant.now().minusSeconds(1800);

        PasswordResetToken resetToken =
                PasswordResetToken.reconstitute(tokenId, userId, token, expiresAt, usedAt, createdAt);

        assertEquals(tokenId, resetToken.tokenId());
        assertEquals(userId, resetToken.userId());
        assertEquals(token, resetToken.token());
        assertEquals(expiresAt, resetToken.expiresAt());
        assertEquals(usedAt, resetToken.usedAt());
        assertTrue(resetToken.isUsed());
        assertEquals(0, resetToken.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should reconstitute unused token from persistence")
    void shouldReconstituteUnusedTokenFromPersistence() {
        ResetTokenId tokenId = ResetTokenId.generate();
        UserId userId = UserId.generate();
        ResetToken token = ResetToken.generate();
        Instant expiresAt = Instant.now().plusSeconds(3600);
        Instant createdAt = Instant.now().minusSeconds(1800);

        PasswordResetToken resetToken = PasswordResetToken.reconstitute(
                tokenId, userId, token, expiresAt, null, // usedAt
                createdAt);

        assertEquals(tokenId, resetToken.tokenId());
        assertEquals(userId, resetToken.userId());
        assertEquals(token, resetToken.token());
        assertEquals(expiresAt, resetToken.expiresAt());
        assertNull(resetToken.usedAt());
        assertFalse(resetToken.isUsed());
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        var domainEvents = resetToken.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof PasswordResetRequested);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        var domainEvents = resetToken.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should have unique token IDs")
    void shouldHaveUniqueTokenIds() {
        UserId userId = UserId.generate();

        PasswordResetToken token1 = PasswordResetToken.create(userId);
        PasswordResetToken token2 = PasswordResetToken.create(userId);

        assertNotEquals(token1.tokenId(), token2.tokenId());
        assertNotEquals(token1.token(), token2.token());
    }

    @Test
    @DisplayName("Should set usedAt timestamp when marked as used")
    void shouldSetUsedAtTimestampWhenMarkedAsUsed() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        Instant beforeMarkAsUsed = Instant.now();
        resetToken.markAsUsed();
        Instant afterMarkAsUsed = Instant.now();

        assertNotNull(resetToken.usedAt());
        assertTrue(resetToken.usedAt().isAfter(beforeMarkAsUsed.minusMillis(100)));
        assertTrue(resetToken.usedAt().isBefore(afterMarkAsUsed.plusMillis(100)));
    }

    @Test
    @DisplayName("Should handle default 24 hour expiration")
    void shouldHandleDefault24HourExpiration() {
        UserId userId = UserId.generate();
        PasswordResetToken resetToken = PasswordResetToken.create(userId);

        long expectedExpirationSeconds = 24 * 3600L;
        long actualExpirationSeconds =
                resetToken.expiresAt().getEpochSecond() - resetToken.createdAt().getEpochSecond();
        // Allow 1 second tolerance
        assertTrue(Math.abs(actualExpirationSeconds - expectedExpirationSeconds) <= 1);
    }
}
