package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.commands.RegisterUserCommand;
import com.k12.platform.domain.model.exceptions.UserAlreadyExistsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.service.UserRegistrationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for UserResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("UserResource REST Tests")
class UserResourceTest {

    @InjectMock
    UserRegistrationService userRegistrationService;

    @Test
    @DisplayName("Should create teacher successfully")
    void shouldCreateTeacherSuccessfully() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("teacher@example.com"),
                PasswordHash.hash("SecurePass123!"),
                "John",
                "Doe",
                UserRole.TEACHER);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"teacher@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(201))
                .body("email", is("teacher@example.com"))
                .body("role", is("TEACHER"))
                .body("firstName", is("John"))
                .body("lastName", is("Doe"))
                .body("isActive", is(true));
    }

    @Test
    @DisplayName("Should create parent successfully")
    void shouldCreateParentSuccessfully() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("parent@example.com"),
                PasswordHash.hash("SecurePass123!"),
                "Jane",
                "Smith",
                UserRole.PARENT);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"parent@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"Jane\",\"lastName\":\"Smith\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/parent")
                .then()
                .statusCode(is(201))
                .body("email", is("parent@example.com"))
                .body("role", is("PARENT"));
    }

    @Test
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("admin@example.com"),
                PasswordHash.hash("SecurePass123!"),
                "Admin",
                "User",
                UserRole.ADMIN);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"admin@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"Admin\",\"lastName\":\"User\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/admin")
                .then()
                .statusCode(is(201))
                .body("email", is("admin@example.com"))
                .body("role", is("ADMIN"));
    }

    @Test
    @DisplayName("Should return 400 when email is missing")
    void shouldReturn400WhenEmailMissing() {
        String requestBody = "{\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: email"));
    }

    @Test
    @DisplayName("Should return 400 when password is missing")
    void shouldReturn400WhenPasswordMissing() {
        String requestBody = "{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: password"));
    }

    @Test
    @DisplayName("Should return 400 when firstName is missing")
    void shouldReturn400WhenFirstNameMissing() {
        String requestBody = "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: firstName"));
    }

    @Test
    @DisplayName("Should return 400 when lastName is missing")
    void shouldReturn400WhenLastNameMissing() {
        String requestBody = "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"John\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: lastName"));
    }

    @Test
    @DisplayName("Should return 500 for invalid email format (BUG: should be 400)")
    void shouldReturn500ForInvalidEmailFormat() {
        String requestBody =
                "{\"email\":\"invalidemail\",\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should return 400 for weak password")
    void shouldReturn400ForWeakPassword() {
        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Password must be at least 8 characters"));
    }

    @Test
    @DisplayName("Should return 409 when user already exists")
    void shouldReturn409WhenUserExists() {
        // Arrange
        when(userRegistrationService.register(any(RegisterUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException(EmailAddress.of("test@example.com")));

        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(409))
                .body("message", is("User with this email already exists"));
    }

    @Test
    @DisplayName("Should handle JSON content type")
    void shouldHandleJsonContentType() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("SecurePass123!"),
                "John",
                "Doe",
                UserRole.TEACHER);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("user+tag@example.com"),
                PasswordHash.hash("SecurePass123!"),
                "John",
                "Doe",
                UserRole.PARENT);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"user+tag@example.com\",\"password\":\"SecurePass123!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/parent")
                .then()
                .statusCode(is(201))
                .body("email", is("user+tag@example.com"));
    }

    @Test
    @DisplayName("Should accept strong password")
    void shouldAcceptStrongPassword() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("VeryStrongPassword123!@#"),
                "John",
                "Doe",
                UserRole.ADMIN);

        when(userRegistrationService.register(any(RegisterUserCommand.class))).thenReturn(mockUser);

        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"VeryStrongPassword123!@#\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/admin")
                .then()
                .statusCode(is(201))
                .body("email", is("test@example.com"));
    }

    @Test
    @DisplayName("Should return 400 for password too short")
    void shouldReturn400ForPasswordTooShort() {
        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"Pass1!\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Password must be at least 8 characters"));
    }

    @Test
    @DisplayName("Should return 400 for blank password")
    void shouldReturn400ForBlankPassword() {
        // Blank password (only spaces) is caught by the resource's isBlank() check
        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"        \",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: password"));
    }

    @Test
    @DisplayName("Should return 400 for empty password")
    void shouldReturn400ForEmptyPassword() {
        // Empty password is caught by the resource's isBlank() check
        String requestBody =
                "{\"email\":\"test@example.com\",\"password\":\"\",\"firstName\":\"John\",\"lastName\":\"Doe\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/teacher")
                .then()
                .statusCode(is(400))
                .body("message", is("Missing required field: password"));
    }
}
