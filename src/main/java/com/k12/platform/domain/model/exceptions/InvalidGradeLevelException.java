package com.k12.platform.domain.model.exceptions;

/**
 * Domain exception for invalid grade level.
 */
public class InvalidGradeLevelException extends DomainException {
    public InvalidGradeLevelException(String message) {
        super(message);
    }
}
