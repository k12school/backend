package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a user is deactivated.
 */
public record UserDeactivated(UserId userId, Instant deactivatedAt) {
    public UserDeactivated {
        if (deactivatedAt == null) {
            deactivatedAt = Instant.now();
        }
    }
}
