package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a user changes password.
 */
public record UserPasswordChanged(UserId userId, Instant changedAt) {
    public UserPasswordChanged {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }
}
