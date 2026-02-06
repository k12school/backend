package com.k12.platform.domain.service;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.commands.LoginCommand;
import com.k12.platform.domain.model.exceptions.AccountDeactivatedException;
import com.k12.platform.domain.model.exceptions.InvalidCredentialsException;
import com.k12.platform.domain.port.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Domain service for authentication.
 * Handles login logic.
 * NOTE: @ApplicationScoped required for CDI injection in Quarkus.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Authenticate user with email and password.
     * @return User if authentication successful
     * @throws InvalidCredentialsException if credentials invalid
     * @throws AccountDeactivatedException if account deactivated
     */
    public User login(LoginCommand command) {
        Optional<User> userOpt = userRepository.findByEmail(command.email());

        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = userOpt.get();

        // Check if account is active
        if (!user.isActive()) {
            throw new AccountDeactivatedException();
        }

        // Verify password
        boolean passwordMatches = user.login(command.plainPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        // Update last login
        userRepository.save(user);

        return user;
    }
}
