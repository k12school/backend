package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;

/**
 * Domain event emitted when a user is activated.
 */
public record UserActivated(UserId userId, Instant activatedAt) {
    public UserActivated {
        if (activatedAt == null) {
            activatedAt = Instant.now();
        }
    }
}
