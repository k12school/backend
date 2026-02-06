package com.k12.platform.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.commands.LoginCommand;
import com.k12.platform.domain.model.exceptions.AccountDeactivatedException;
import com.k12.platform.domain.model.exceptions.InvalidCredentialsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.port.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for AuthenticationService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;

    AuthenticationService authenticationService;

    private User activeUser;
    private User deactivatedUser;
    private EmailAddress testEmail;
    private PasswordHash testPasswordHash;
    private String plainPassword = "SecurePass123!";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository);
        testEmail = EmailAddress.of("test@example.com");
        testPasswordHash = PasswordHash.hash(plainPassword);

        activeUser = User.register(testEmail, testPasswordHash, "John", "Doe", UserRole.TEACHER);
        doNothing().when(userRepository).save(any(User.class));

        deactivatedUser = User.register(testEmail, testPasswordHash, "Jane", "Doe", UserRole.PARENT);
        deactivatedUser.deactivate();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        LoginCommand command = new LoginCommand(testEmail, plainPassword);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(activeUser));

        User result = authenticationService.login(command);

        assertNotNull(result);
        assertEquals(testEmail, result.email());
        verify(userRepository).save(activeUser);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user not found")
    void shouldThrowInvalidCredentialsWhenUserNotFound() {
        LoginCommand command = new LoginCommand(testEmail, plainPassword);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(command));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException with wrong password")
    void shouldThrowInvalidCredentialsWithWrongPassword() {
        LoginCommand command = new LoginCommand(testEmail, "WrongPassword123!");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(activeUser));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(command));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw AccountDeactivatedException for deactivated user")
    void shouldThrowAccountDeactivatedException() {
        LoginCommand command = new LoginCommand(testEmail, plainPassword);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(deactivatedUser));

        assertThrows(AccountDeactivatedException.class, () -> authenticationService.login(command));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException with null password")
    void shouldThrowInvalidCredentialsWithNullPassword() {
        LoginCommand command = new LoginCommand(testEmail, null);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(activeUser));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(command));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update last login on successful authentication")
    void shouldUpdateLastLoginOnSuccessfulAuthentication() {
        LoginCommand command = new LoginCommand(testEmail, plainPassword);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(activeUser));

        User result = authenticationService.login(command);

        assertNotNull(result.lastLoginAt());
        verify(userRepository).save(activeUser);
    }

    @Test
    @DisplayName("Should handle login for all user roles")
    void shouldHandleLoginForAllUserRoles() {
        UserRole[] roles = {UserRole.ADMIN, UserRole.TEACHER, UserRole.PARENT};

        for (UserRole role : roles) {
            User user = User.register(testEmail, testPasswordHash, "Test", "User", role);
            LoginCommand command = new LoginCommand(testEmail, plainPassword);
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

            User result = authenticationService.login(command);

            assertNotNull(result);
            assertEquals(role, result.role());
        }
    }
}
