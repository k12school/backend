package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Password reset token aggregate root.
 * Manages password reset tokens with expiration and usage tracking.
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 */
public final class PasswordResetToken {

    private final ResetTokenId tokenId;
    private final UserId userId;
    private final ResetToken token;
    private final Instant expiresAt;
    private Instant usedAt;
    private final Instant createdAt;
    private boolean isUsed = false;

    private static final int DEFAULT_EXPIRATION_HOURS = 24;

    private PasswordResetToken(ResetTokenId tokenId, UserId userId, ResetToken token, Instant expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    /**
     * Factory method to create a new password reset token.
     */
    public static PasswordResetToken create(UserId userId) {
        return create(userId, DEFAULT_EXPIRATION_HOURS);
    }

    /**
     * Factory method to create a new password reset token with custom expiration.
     */
    public static PasswordResetToken create(UserId userId, int hoursUntilExpiration) {
        Instant expiresAt = Instant.now().plusSeconds(hoursUntilExpiration * 3600L);

        PasswordResetToken resetToken =
                new PasswordResetToken(ResetTokenId.generate(), userId, ResetToken.generate(), expiresAt);

        resetToken.recordDomainEvent(new PasswordResetRequested(
                resetToken.tokenId, resetToken.userId, resetToken.token, resetToken.expiresAt, null));

        return resetToken;
    }

    /**
     * Factory method to reconstitute from persistence.
     */
    public static PasswordResetToken reconstitute(
            ResetTokenId tokenId,
            UserId userId,
            ResetToken token,
            Instant expiresAt,
            Instant usedAt,
            Instant createdAt) {
        PasswordResetToken resetToken = new PasswordResetToken(tokenId, userId, token, expiresAt);
        resetToken.usedAt = usedAt;
        if (usedAt != null) {
            resetToken.isUsed = true;
        }
        return resetToken;
    }

    /**
     * Mark the token as used.
     */
    public void markAsUsed() {
        if (this.isUsed) {
            throw new IllegalStateException("Token has already been used");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot mark expired token as used");
        }
        this.usedAt = Instant.now();
        this.isUsed = true;
    }

    /**
     * Check if the token is expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Check if the token is valid (not expired and not used).
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    private void recordDomainEvent(Object event) {
        this.domainEvents.add(event);
    }

    private final List<Object> domainEvents = new ArrayList<>();

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public ResetTokenId tokenId() {
        return tokenId;
    }

    public UserId userId() {
        return userId;
    }

    public ResetToken token() {
        return token;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant usedAt() {
        return usedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
