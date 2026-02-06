package com.k12.platform.interfaces.rest.jwt;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.UserId;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for generating JWT tokens.
 * Simplified implementation for testing.
 */
@ApplicationScoped
public class TokenService {

    // Simple Base64 encoder for demo purposes
    private String base64Encode(String input) {
        return java.util.Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        // For now, return a simple token format (user:timestamp)
        // In production, use proper JWT signing
        long expiration = Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli();

        String tokenContent =
                user.userId().toString() + ":" + expiration + ":" + user.email().value();
        return base64Encode(tokenContent);
    }

    public UserId extractUserId(String token) {
        // Extract user ID from simple token format
        String decoded = new String(java.util.Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split(":", 3);
        return UserId.of(parts[0]);
    }
}
