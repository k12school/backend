package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.PasswordResetToken;
import com.k12.platform.domain.model.PasswordResetTokenRepository;
import com.k12.platform.domain.model.valueobjects.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of PasswordResetTokenRepository port.
 */
@ApplicationScoped
public class JpaPasswordResetTokenAdapter implements PasswordResetTokenRepository {

    @Override
    @Transactional
    public void save(PasswordResetToken resetToken) {
        PanachePasswordResetTokenEntity existing =
                PanachePasswordResetTokenEntity.findById(resetToken.tokenId().value());

        if (existing == null) {
            PanachePasswordResetTokenEntity entity = toEntity(resetToken);
            entity.setCreatedAt(Instant.now());
            entity.persist();
        } else {
            updateFromDomain(existing, resetToken);
        }
    }

    @Override
    public Optional<PasswordResetToken> findById(ResetTokenId tokenId) {
        PanachePasswordResetTokenEntity entity = PanachePasswordResetTokenEntity.findById(tokenId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(ResetToken token) {
        PanachePasswordResetTokenEntity entity =
                PanachePasswordResetTokenEntity.find("token", token.value()).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
    }

    @Override
    public List<PasswordResetToken> findByUserId(UserId userId) {
        List<PanachePasswordResetTokenEntity> entities = PanachePasswordResetTokenEntity.list("userId", userId.value());
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void delete(ResetTokenId tokenId) {
        PanachePasswordResetTokenEntity.deleteById(tokenId.value());
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        PanachePasswordResetTokenEntity.delete("expiresAt < ?1", Instant.now());
    }

    private PanachePasswordResetTokenEntity toEntity(PasswordResetToken resetToken) {
        PanachePasswordResetTokenEntity entity = new PanachePasswordResetTokenEntity();
        entity.setId(resetToken.tokenId().value());
        entity.setUserId(resetToken.userId().value());
        entity.setToken(resetToken.token().value());
        entity.setExpiresAt(resetToken.expiresAt());
        entity.setUsedAt(resetToken.usedAt());
        return entity;
    }

    private void updateFromDomain(PanachePasswordResetTokenEntity entity, PasswordResetToken resetToken) {
        entity.setUsedAt(resetToken.usedAt());
    }

    private PasswordResetToken toDomain(PanachePasswordResetTokenEntity entity) {
        return PasswordResetToken.reconstitute(
                ResetTokenId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                ResetToken.of(entity.getToken()),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                entity.getCreatedAt());
    }
}
