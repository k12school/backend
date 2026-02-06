package com.k12.platform.infrastructure.persistence;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.model.valueobjects.UserId;
import com.k12.platform.domain.port.UserRepository;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA persistence adapter implementing UserRepository port.
 * Maps domain model ↔ Panache entity.
 * Framework imports allowed ONLY in infrastructure layer.
 */
@ApplicationScoped
@Unremovable
public class JpaUserAdapter implements UserRepository {

    @Override
    @Transactional
    public void save(User user) {
        PanacheUserEntity entity = toEntity(user);

        PanacheUserEntity existing = PanacheUserEntity.findById(entity.getId());
        if (existing == null) {
            entity.persist();
        } else {
            // Update existing entity fields
            existing.setEmail(entity.getEmail());
            existing.setPasswordHash(entity.getPasswordHash());
            existing.setFirstName(entity.getFirstName());
            existing.setLastName(entity.getLastName());
            existing.setRole(entity.getRole());
            existing.setActive(entity.isActive());
            existing.setLastLoginAt(entity.getLastLoginAt());
            existing.persist();
        }
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return Optional.ofNullable(PanacheUserEntity.findById(userId.value()))
                .map(e -> (PanacheUserEntity) e)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return Optional.ofNullable((PanacheUserEntity)
                        PanacheUserEntity.find("email", email.value()).firstResult())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return PanacheUserEntity.count("email", email.value()) > 0;
    }

    @Override
    @Transactional
    public void delete(UserId userId) {
        PanacheUserEntity.deleteById(userId.value());
    }

    @Override
    public List<User> findAll() {
        return PanacheUserEntity.<PanacheUserEntity>listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Map domain User to Panache entity.
     */
    private PanacheUserEntity toEntity(User user) {
        PanacheUserEntity entity = new PanacheUserEntity();
        entity.setId(user.userId().value());
        entity.setEmail(user.email().value());
        entity.setPasswordHash(user.passwordHash().value());
        entity.setFirstName(user.firstName());
        entity.setLastName(user.lastName());
        entity.setRole(user.role().name());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.createdAt());
        entity.setUpdatedAt(user.createdAt());
        entity.setLastLoginAt(user.lastLoginAt());
        return entity;
    }

    /**
     * Map Panache entity to domain User.
     */
    private User toDomain(PanacheUserEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                EmailAddress.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()),
                entity.getFirstName(),
                entity.getLastName(),
                UserRole.valueOf(entity.getRole()),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getLastLoginAt());
    }
}
