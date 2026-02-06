package com.k12.platform.domain.port;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.Optional;

/**
 * Port for user repository.
 * Defined in domain, implemented in infrastructure.
 * Expresses intent (save, findByEmail), not technology.
 */
public interface UserRepository {

    /**
     * Save a user (create or update).
     */
    void save(User user);

    /**
     * Find user by ID.
     */
    Optional<User> findById(UserId userId);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(EmailAddress email);

    /**
     * Check if user exists by email.
     */
    boolean existsByEmail(EmailAddress email);

    /**
     * Delete a user by ID.
     */
    void delete(UserId userId);

    /**
     * Find all users.
     */
    java.util.List<User> findAll();
}
