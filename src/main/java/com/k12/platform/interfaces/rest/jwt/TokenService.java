package com.k12.platform.interfaces.rest.jwt;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.UserId;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
                .expiresIn(24, ChronoUnit.HOURS)
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
        // Extract subject claim which contains the user ID
        String subject = Jwt.claims(token).getSubject();
        return UserId.of(subject);
    }
}
