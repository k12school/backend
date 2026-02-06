package com.k12.platform.domain.model.exceptions;

/**
 * Domain exception for invalid login credentials.
 */
public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
