package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a user is registered.
 */
public record UserRegistered(UserId userId, EmailAddress email, UserRole role, Instant occurredAt) {
    public UserRegistered {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
