package com.k12.platform.interfaces.rest.dto;

/**
 * REST response DTO for successful login.
 */
public class LoginResponse {
    private String token;
    private UserResponse user;

    public LoginResponse() {}

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
