package com.k12.platform.domain.model.exceptions;

/**
 * Domain exception for invalid email format.
 */
public class InvalidEmailException extends DomainException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
