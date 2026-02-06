package com.k12.platform.domain.model.commands;

import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;

/**
 * Command representing intent to register a new user.
 */
public record RegisterUserCommand(
        EmailAddress email, PasswordHash passwordHash, String firstName, String lastName, UserRole role) {}
