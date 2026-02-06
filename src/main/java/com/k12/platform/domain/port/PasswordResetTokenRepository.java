package com.k12.platform.domain.port;

import com.k12.platform.domain.model.PasswordResetToken;
import com.k12.platform.domain.model.valueobjects.ResetToken;
import com.k12.platform.domain.model.valueobjects.ResetTokenId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Port for PasswordResetToken persistence operations.
 */
public interface PasswordResetTokenRepository {
    void save(PasswordResetToken token);

    Optional<PasswordResetToken> findById(ResetTokenId tokenId);

    Optional<PasswordResetToken> findByToken(ResetToken token);

    List<PasswordResetToken> findByUserId(UserId userId);

    void delete(ResetTokenId tokenId);

    void deleteExpiredTokens();
}
