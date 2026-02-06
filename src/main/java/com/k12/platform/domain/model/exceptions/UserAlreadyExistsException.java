package com.k12.platform.domain.model.exceptions;

import com.k12.platform.domain.model.valueobjects.EmailAddress;

/**
 * Domain exception when user already exists.
 */
public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(EmailAddress email) {
        super("User with email " + email + " already exists");
    }
}
