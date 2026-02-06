package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User aggregate root.
 * Enforces invariants: email uniqueness, password requirements, activation status.
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 * No imports from: io.quarkus, jakarta.*, jakarta.persistence, jakarta.transaction, etc.
 */
public final class User {

    private final UserId userId;
    private final EmailAddress email;
    private PasswordHash passwordHash;
    private final String firstName;
    private final String lastName;
    private final UserRole role;
    private boolean isActive;
    private Instant createdAt;
    private Instant lastLoginAt;
    private final List<Object> domainEvents = new ArrayList<>();

    private User(
            UserId userId,
            EmailAddress email,
            PasswordHash passwordHash,
            String firstName,
            String lastName,
            UserRole role,
            boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = Instant.now();
        this.lastLoginAt = null;
    }

    /**
     * Factory method to register a new user.
     */
    public static User register(
            EmailAddress email, PasswordHash passwordHash, String firstName, String lastName, UserRole role) {
        User user = new User(
                UserId.generate(),
                email,
                passwordHash,
                firstName,
                lastName,
                role,
                true // new users are active by default
                );

        user.recordDomainEvent(new UserRegistered(user.userId, user.email, user.role, user.createdAt));

        return user;
    }

    /**
     * Factory method to reconstitute user from persistence.
     */
    public static User reconstitute(
            UserId userId,
            EmailAddress email,
            PasswordHash passwordHash,
            String firstName,
            String lastName,
            UserRole role,
            boolean isActive,
            Instant createdAt,
            Instant lastLoginAt) {
        User user = new User(userId, email, passwordHash, firstName, lastName, role, isActive);
        user.createdAt = createdAt;
        user.lastLoginAt = lastLoginAt;
        return user;
    }

    /**
     * Verifies password and updates last login if successful.
     */
    public boolean login(String plainPassword) {
        if (!isActive) {
            return false;
        }

        boolean passwordMatches = passwordHash.verify(plainPassword);
        if (passwordMatches) {
            this.lastLoginAt = Instant.now();
            this.recordDomainEvent(new UserLoggedIn(this.userId, this.lastLoginAt));
        }
        return passwordMatches;
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        if (!isActive) {
            throw new IllegalStateException("User is already deactivated");
        }
        this.isActive = false;
        this.recordDomainEvent(new UserDeactivated(this.userId, Instant.now()));
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        if (isActive) {
            throw new IllegalStateException("User is already active");
        }
        this.isActive = true;
        this.recordDomainEvent(new UserActivated(this.userId, Instant.now()));
    }

    /**
     * Updates password.
     */
    public void updatePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.recordDomainEvent(new UserPasswordChanged(this.userId, Instant.now()));
    }

    private void recordDomainEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public UserId userId() {
        return userId;
    }

    public EmailAddress email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public UserRole role() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant lastLoginAt() {
        return lastLoginAt;
    }

    // NO setters exposing mutable state - all state changes through behavior methods
}
