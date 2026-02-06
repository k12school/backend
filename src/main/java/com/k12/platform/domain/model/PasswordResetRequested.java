package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.ResetToken;
import com.k12.platform.domain.model.valueobjects.ResetTokenId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a password reset is requested.
 */
public record PasswordResetRequested(
        ResetTokenId tokenId, UserId userId, ResetToken token, Instant expiresAt, Instant occurredAt) {
    public PasswordResetRequested {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
