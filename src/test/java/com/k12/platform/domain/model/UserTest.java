package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.k12.platform.domain.model.valueobjects.*;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for User aggregate.
 * Target: 100% coverage
 */
@DisplayName("User Aggregate Tests")
class UserTest {

    private EmailAddress email;
    private PasswordHash passwordHash;
    private String firstName;
    private String lastName;
    private UserRole role;

    @BeforeEach
    void setUp() {
        email = EmailAddress.of("test@example.com");
        passwordHash = PasswordHash.hash("SecurePass123!");
        firstName = "John";
        lastName = "Doe";
        role = UserRole.TEACHER;
    }

    @Test
    @DisplayName("Should register new user")
    void shouldRegisterNewUser() {
        User user = User.register(email, passwordHash, firstName, lastName, role);

        assertNotNull(user.userId());
        assertEquals(email, user.email());
        assertEquals(passwordHash, user.passwordHash());
        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());
        assertEquals(role, user.role());
        assertTrue(user.isActive());
        assertNotNull(user.createdAt());
        // lastLoginAt is null on registration
        assertNull(user.lastLoginAt());
    }

    @Test
    @DisplayName("Should record UserRegistered event on registration")
    void shouldRecordUserRegisteredEvent() {
        User user = User.register(email, passwordHash, firstName, lastName, role);

        var domainEvents = user.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof UserRegistered);
    }

    @Test
    @DisplayName("Should login with correct password")
    void shouldLoginWithCorrectPassword() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        var initialEventCount = user.getDomainEvents().size();

        boolean loggedIn = user.login("SecurePass123!");

        assertTrue(loggedIn);
        assertNotNull(user.lastLoginAt());
        assertEquals(initialEventCount + 1, user.getDomainEvents().size());
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void shouldFailLoginWithIncorrectPassword() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        var initialEventCount = user.getDomainEvents().size();

        boolean loggedIn = user.login("WrongPassword123!");

        assertFalse(loggedIn);
        assertEquals(initialEventCount, user.getDomainEvents().size()); // No new event
    }

    @Test
    @DisplayName("Should fail login with null password")
    void shouldFailLoginWithNullPassword() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        var initialEventCount = user.getDomainEvents().size();

        boolean loggedIn = user.login(null);

        assertFalse(loggedIn);
        assertEquals(initialEventCount, user.getDomainEvents().size()); // No new event
    }

    @Test
    @DisplayName("Should record UserLoggedIn event on login")
    void shouldRecordUserLoggedInEvent() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        // Don't clear - just check the event was added
        var initialEventCount = user.getDomainEvents().size();

        user.login("SecurePass123!");

        assertTrue(user.getDomainEvents().size() > initialEventCount);
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        var initialEventCount = user.getDomainEvents().size();

        user.deactivate();

        assertFalse(user.isActive());
        assertTrue(user.getDomainEvents().size() > initialEventCount);
    }

    @Test
    @DisplayName("Should activate deactivated user")
    void shouldActivateDeactivatedUser() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        user.deactivate();
        var initialEventCount = user.getDomainEvents().size();

        user.activate();

        assertTrue(user.isActive());
        assertTrue(user.getDomainEvents().size() > initialEventCount);
    }

    @Test
    @DisplayName("Should update password")
    void shouldUpdatePassword() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        String oldPassword = "SecurePass123!";
        String newPassword = "NewSecurePass456!";
        var initialEventCount = user.getDomainEvents().size();

        user.updatePassword(PasswordHash.hash(newPassword));

        // Old password should no longer work
        assertFalse(user.login(oldPassword));
        // New password should work
        assertTrue(user.login(newPassword));
        assertTrue(user.getDomainEvents().size() > initialEventCount);
    }

    @Test
    @DisplayName("Should reconstitute user from persistence")
    void shouldReconstituteUserFromPersistence() {
        UserId userId = UserId.generate();
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant lastLoginAt = Instant.now().minusSeconds(1800);

        User user = User.reconstitute(
                userId,
                email,
                passwordHash,
                firstName,
                lastName,
                role,
                true, // isActive
                createdAt,
                lastLoginAt);

        assertEquals(userId, user.userId());
        assertEquals(email, user.email());
        assertTrue(user.isActive());
        assertEquals(createdAt, user.createdAt());
        assertEquals(lastLoginAt, user.lastLoginAt());
        assertEquals(0, user.getDomainEvents().size()); // No events on reconstitution
    }

    @Test
    @DisplayName("Should get domain events")
    void shouldGetDomainEvents() {
        User user = User.register(email, passwordHash, firstName, lastName, role);

        var domainEvents = user.getDomainEvents();
        assertFalse(domainEvents.isEmpty());
        assertTrue(domainEvents.get(0) instanceof UserRegistered);
    }

    @Test
    @DisplayName("Should have unmodifiable domain events")
    void shouldHaveUnmodifiableDomainEvents() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        var domainEvents = user.getDomainEvents();

        assertThrows(UnsupportedOperationException.class, () -> domainEvents.clear());
    }

    @Test
    @DisplayName("Should not allow deactivated user to login")
    void shouldNotAllowDeactivatedUserToLogin() {
        User user = User.register(email, passwordHash, firstName, lastName, role);
        user.deactivate();

        boolean loggedIn = user.login("SecurePass123!");

        assertFalse(loggedIn);
    }

    @Test
    @DisplayName("Should handle all user roles")
    void shouldHandleAllUserRoles() {
        User admin = User.register(email, passwordHash, firstName, lastName, UserRole.ADMIN);
        User teacher = User.register(email, passwordHash, firstName, lastName, UserRole.TEACHER);
        User parent = User.register(email, passwordHash, firstName, lastName, UserRole.PARENT);

        assertEquals(UserRole.ADMIN, admin.role());
        assertEquals(UserRole.TEACHER, teacher.role());
        assertEquals(UserRole.PARENT, parent.role());
    }

    @Test
    @DisplayName("Should get user properties")
    void shouldGetUserProperties() {
        User user = User.register(email, passwordHash, firstName, lastName, role);

        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());
        assertEquals(email, user.email());
        assertEquals(passwordHash, user.passwordHash());
        assertEquals(role, user.role());
        assertTrue(user.isActive());
    }
}
