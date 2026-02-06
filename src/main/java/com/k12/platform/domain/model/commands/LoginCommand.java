package com.k12.platform.domain.model.commands;

import com.k12.platform.domain.model.valueobjects.EmailAddress;

/**
 * Command representing intent to login.
 */
public record LoginCommand(EmailAddress email, String plainPassword) {}
