package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a user logs in.
 */
public record UserLoggedIn(UserId userId, Instant loggedAt) {
    public UserLoggedIn {
        if (loggedAt == null) {
            loggedAt = Instant.now();
        }
    }
}
