package com.k12.platform.domain.model.valueobjects;

import com.k12.platform.domain.model.exceptions.InvalidEmailException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an email address.
 * Immutable and self-validating.
 */
public final class EmailAddress {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final String value;

    private EmailAddress(String value) {
        if (!isValid(value)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
        this.value = value;
    }

    public static EmailAddress of(String email) {
        return new EmailAddress(email);
    }

    private static boolean isValid(String email) {
        return email != null && !email.isBlank() && EMAIL_PATTERN.matcher(email).matches();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
