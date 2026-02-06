package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.commands.LoginCommand;
import com.k12.platform.domain.model.exceptions.AccountDeactivatedException;
import com.k12.platform.domain.model.exceptions.InvalidCredentialsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.service.AuthenticationService;
import com.k12.platform.interfaces.rest.jwt.TokenService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for AuthResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("AuthResource REST Tests")
class AuthResourceTest {

    @InjectMock
    AuthenticationService authenticationService;

    @InjectMock
    TokenService tokenService;

    @Test
    @DisplayName("Should return 400 when email is missing")
    void shouldReturn400WhenEmailMissing() {
        String requestBody = "{\"email\":\"\",\"password\":\"password123\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Email is required"));
    }

    @Test
    @DisplayName("Should return 400 when password is missing")
    void shouldReturn400WhenPasswordMissing() {
        String requestBody = "{\"email\":\"test@example.com\",\"password\":\"\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Password is required"));
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    void shouldReturn400WhenEmailFormatInvalid() {
        String requestBody = "{\"email\":\"invalidemail\",\"password\":\"password123\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid email format"));
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void shouldReturn401ForInvalidCredentials() {
        // Arrange
        when(authenticationService.login(any(LoginCommand.class))).thenThrow(new InvalidCredentialsException());

        String requestBody = "{\"email\":\"nonexistent@example.com\",\"password\":\"wrongpassword\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(401))
                .body("message", is("Invalid credentials"));
    }

    @Test
    @DisplayName("Should return 200 and token for valid credentials")
    void shouldAcceptValidEmailFormat() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("valid@example.com"),
                PasswordHash.hash("password123"),
                "John",
                "Doe",
                UserRole.TEACHER);

        when(authenticationService.login(any(LoginCommand.class))).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn("mock-jwt-token");

        String requestBody = "{\"email\":\"valid@example.com\",\"password\":\"password123\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(200))
                .body("token", is("mock-jwt-token"))
                .body("user.email", is("valid@example.com"))
                .body("user.role", is("TEACHER"))
                .body("user.firstName", is("John"))
                .body("user.lastName", is("Doe"))
                .body("user.active", is(true));
    }

    @Test
    @DisplayName("Should reject email without @ symbol")
    void shouldRejectEmailWithoutAtSymbol() {
        String requestBody = "{\"email\":\"invalidemail.com\",\"password\":\"password123\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid email format"));
    }

    @Test
    @DisplayName("Should reject email without domain")
    void shouldRejectEmailWithoutDomain() {
        String requestBody = "{\"email\":\"test@\",\"password\":\"password123\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid email format"));
    }

    @Test
    @DisplayName("Should return error response with message field")
    void shouldReturnErrorResponseWithMessageField() {
        String requestBody = "{\"email\":\"\",\"password\":\"\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(400))
                .body("message", is("Email is required"));
    }

    @Test
    @DisplayName("Should return 401 for deactivated account")
    void shouldReturn401ForDeactivatedAccount() {
        // Arrange
        when(authenticationService.login(any(LoginCommand.class))).thenThrow(new AccountDeactivatedException());

        String requestBody = "{\"email\":\"deactivated@example.com\",\"password\":\"password123\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(401))
                .body("message", is("Account is deactivated"));
    }

    @Test
    @DisplayName("Should return 200 for admin user")
    void shouldReturn200ForAdminUser() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("admin@example.com"), PasswordHash.hash("admin123"), "Admin", "User", UserRole.ADMIN);

        when(authenticationService.login(any(LoginCommand.class))).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn("admin-token");

        String requestBody = "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(200))
                .body("token", is("admin-token"))
                .body("user.role", is("ADMIN"));
    }

    @Test
    @DisplayName("Should return 200 for parent user")
    void shouldReturn200ForParentUser() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("parent@example.com"),
                PasswordHash.hash("parent123"),
                "Jane",
                "Smith",
                UserRole.PARENT);

        when(authenticationService.login(any(LoginCommand.class))).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn("parent-token");

        String requestBody = "{\"email\":\"parent@example.com\",\"password\":\"parent123\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(is(200))
                .body("token", is("parent-token"))
                .body("user.role", is("PARENT"));
    }
}
