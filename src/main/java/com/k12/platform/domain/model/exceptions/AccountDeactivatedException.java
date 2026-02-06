package com.k12.platform.domain.model.exceptions;

/**
 * Domain exception when account is deactivated.
 */
public class AccountDeactivatedException extends DomainException {
    public AccountDeactivatedException() {
        super("Account is deactivated");
    }
}
