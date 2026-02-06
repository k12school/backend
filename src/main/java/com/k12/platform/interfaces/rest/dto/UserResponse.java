package com.k12.platform.interfaces.rest.dto;

/**
 * REST response DTO for user data.
 */
public class UserResponse {
    private String id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private boolean isActive;

    public UserResponse() {}

    public UserResponse(String id, String email, String role, String firstName, String lastName, boolean isActive) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
