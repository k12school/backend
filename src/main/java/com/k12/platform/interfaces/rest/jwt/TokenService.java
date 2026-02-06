package com.k12.platform.interfaces.rest.jwt;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.UserId;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.eclipse.microprofile.jwt.Claims;

/**
 * Service for generating and parsing JWT tokens using SmallRye JWT.
 */
@ApplicationScoped
public class TokenService {

    /**
     * Generate a signed JWT token for the authenticated user.
     *
     * @param user the user to generate token for
     * @return signed JWT string
     */
    public String generateToken(User user) {
        return Jwt.upn(user.email().value())
                .subject(user.userId().toString())
                .claim(Claims.groups.name(), Set.of(user.role().name()))
                .claim("email", user.email().value())
                .claim("firstName", user.firstName())
                .claim("lastName", user.lastName())
                .issuedAt(Instant.now())
                .expiresIn(Duration.ofHours(12))
                .sign();
    }

    /**
     * Extract user ID from JWT token.
     * This is a simplified extraction - in production, JWT validation
     * is handled by the SmallRye JWT extension automatically.
     *
     * @param token raw JWT token string
     * @return user ID
     */
    public UserId extractUserId(String token) {
        // Decode and parse the JWT without verification
        // Note: This is for convenience - actual validation happens
        // when the token is used in authenticated requests
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        // Decode the payload (second part)
        String payload =
                new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);

        // Extract subject from JSON payload
        // Simple parsing for "sub":"value" pattern
        String subPattern = "\"sub\":\"";
        int subIndex = payload.indexOf(subPattern);
        if (subIndex == -1) {
            throw new IllegalArgumentException("Subject claim not found in token");
        }

        int startIndex = subIndex + subPattern.length();
        int endIndex = payload.indexOf("\"", startIndex);
        String subject = payload.substring(startIndex, endIndex);

        return UserId.of(subject);
    }
}
