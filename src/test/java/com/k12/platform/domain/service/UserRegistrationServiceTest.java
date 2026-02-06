package com.k12.platform.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.commands.RegisterUserCommand;
import com.k12.platform.domain.model.exceptions.UserAlreadyExistsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for UserRegistrationService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("UserRegistrationService Tests")
class UserRegistrationServiceTest {

    @Mock
    UserRepository userRepository;

    UserRegistrationService userRegistrationService;

    private EmailAddress testEmail;
    private PasswordHash testPasswordHash;
    private String firstName = "John";
    private String lastName = "Doe";
    private UserRole role = UserRole.TEACHER;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRegistrationService = new UserRegistrationService(userRepository);
        testEmail = EmailAddress.of("test@example.com");
        testPasswordHash = PasswordHash.hash("SecurePass123!");
        doNothing().when(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        User result = userRegistrationService.register(command);

        assertNotNull(result);
        assertEquals(testEmail, result.email());
        assertEquals(firstName, result.firstName());
        assertEquals(lastName, result.lastName());
        assertEquals(role, result.role());
        assertTrue(result.isActive());
        verify(userRepository).existsByEmail(testEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email exists")
    void shouldThrowUserAlreadyExistsException() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userRegistrationService.register(command));

        verify(userRepository).existsByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user with all role types")
    void shouldRegisterUserWithAllRoleTypes() {
        UserRole[] roles = {UserRole.ADMIN, UserRole.TEACHER, UserRole.PARENT};

        for (UserRole testRole : roles) {
            RegisterUserCommand command =
                    new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, testRole);
            when(userRepository.existsByEmail(testEmail)).thenReturn(false);

            User result = userRegistrationService.register(command);

            assertNotNull(result);
            assertEquals(testRole, result.role());
        }
    }

    @Test
    @DisplayName("Should save user after creation")
    void shouldSaveUserAfterCreation() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        userRegistrationService.register(command);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should not save user when email already exists")
    void shouldNotSaveUserWhenEmailExists() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userRegistrationService.register(command));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return created user")
    void shouldReturnCreatedUser() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        User result = userRegistrationService.register(command);

        assertNotNull(result.userId());
        assertNotNull(result.createdAt());
        assertEquals(testEmail, result.email());
    }

    @Test
    @DisplayName("Should check email uniqueness before registration")
    void shouldCheckEmailUniquenessBeforeRegistration() {
        RegisterUserCommand command = new RegisterUserCommand(testEmail, testPasswordHash, firstName, lastName, role);
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userRegistrationService.register(command));

        verify(userRepository).existsByEmail(testEmail);
    }
}
