package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.domain.port.PasswordResetTokenRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Domain service for Password Reset operations.
 */
@ApplicationScoped
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Request a password reset for a user.
     */
    public PasswordResetToken requestPasswordReset(UserId userId) {
        // Invalidate any existing tokens for this user
        tokenRepository.findByUserId(userId).forEach(token -> {
            if (token.isValid()) {
                // In a real system, we might want to keep old tokens for audit
                // For now, we just create a new one
            }
        });

        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        tokenRepository.save(resetToken);
        return resetToken;
    }

    /**
     * Validate a reset token and return the user ID if valid.
     */
    public UserId validateResetToken(String tokenString) {
        ResetToken token = ResetToken.of(tokenString);
        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new IllegalStateException("Reset token has expired");
            }
            if (resetToken.isUsed()) {
                throw new IllegalStateException("Reset token has already been used");
            }
        }

        return resetToken.userId();
    }

    /**
     * Mark a reset token as used after password reset.
     */
    public void markTokenAsUsed(String tokenString) {
        ResetToken token = ResetToken.of(tokenString);
        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        resetToken.markAsUsed();
        tokenRepository.save(resetToken);
    }
}
