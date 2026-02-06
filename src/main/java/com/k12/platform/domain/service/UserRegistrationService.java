package com.k12.platform.domain.service;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.commands.RegisterUserCommand;
import com.k12.platform.domain.model.exceptions.UserAlreadyExistsException;
import com.k12.platform.domain.port.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

/**
 * Domain service for user registration.
 * Orchestrates user creation with uniqueness validation.
 * NOTE: @ApplicationScoped required for CDI injection in Quarkus.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    /**
     * Register a new user.
     * @throws UserAlreadyExistsException if email already exists
     */
    public User register(RegisterUserCommand command) {
        // Check uniqueness via port
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        // Create aggregate
        var user = User.register(
                command.email(), command.passwordHash(), command.firstName(), command.lastName(), command.role());

        // Save via port
        userRepository.save(user);

        return user;
    }
}
