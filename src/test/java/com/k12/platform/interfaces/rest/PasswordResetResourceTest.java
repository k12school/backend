package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.PasswordResetService;
import com.k12.platform.domain.model.PasswordResetToken;
import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.model.valueobjects.UserId;
import com.k12.platform.domain.port.UserRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for PasswordResetResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("PasswordResetResource REST Tests")
class PasswordResetResourceTest {

    @InjectMock
    PasswordResetService passwordResetService;

    @InjectMock
    UserRepository userRepository;

    @Test
    @DisplayName("Should request password reset successfully")
    void shouldRequestPasswordResetEndpointExists() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("OldPassword123!"),
                "Test",
                "User",
                com.k12.platform.domain.model.UserRole.TEACHER);

        PasswordResetToken mockToken = PasswordResetToken.create(mockUser.userId());

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(mockUser));
        when(passwordResetService.requestPasswordReset(any(UserId.class))).thenReturn(mockToken);

        String requestBody = "{\"email\":\"test@example.com\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/request")
                .then()
                .statusCode(is(200))
                .body("message", is("Password reset token generated. In production, this would be sent via email."));
    }

    @Test
    @DisplayName("Should return 200 for non-existent email (security)")
    void shouldReturn200ForNonExistentEmail() {
        // Arrange
        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());

        String requestBody = "{\"email\":\"nonexistent@example.com\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/request")
                .then()
                .statusCode(is(200))
                .body("message", is("If an account exists with this email, a password reset link will be sent."));
    }

    @Test
    @DisplayName("Should return 400 for invalid email format")
    void shouldReturn400ForInvalidEmailFormat() {
        String requestBody = "{\"email\":\"invalidemail\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/request")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should confirm password reset successfully")
    void shouldConfirmPasswordResetEndpointExists() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("OldPassword123!"),
                "Test",
                "User",
                com.k12.platform.domain.model.UserRole.TEACHER);

        UserId userId = mockUser.userId();
        PasswordResetToken mockToken = PasswordResetToken.create(userId);

        when(passwordResetService.validateResetToken(any(String.class))).thenReturn(userId);
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));

        String requestBody = "{\"token\":\"valid-token-123\",\"new_password\":\"NewSecure123!\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(200))
                .body("message", is("Password has been reset successfully"));
    }

    @Test
    @DisplayName("Should return 400 for empty token")
    void shouldReturn400ForInvalidToken() {
        String requestBody = "{\"token\":\"\",\"new_password\":\"NewSecure123!\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should return 400 for weak password")
    void shouldReturn400ForWeakPassword() {
        String requestBody = "{\"token\":\"some-token\",\"new_password\":\"123\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should return 400 for invalid/expired token")
    void shouldReturn404ForExpiredToken() {
        // Arrange
        when(passwordResetService.validateResetToken(any(String.class)))
                .thenThrow(new IllegalArgumentException("Invalid or expired token"));

        String requestBody = "{\"token\":\"expired-token\",\"new_password\":\"NewSecure123!\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid or expired token"));
    }

    @Test
    @DisplayName("Should handle valid email format")
    void shouldHandleValidEmailFormat() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("user@example.com"),
                PasswordHash.hash("OldPassword123!"),
                "Test",
                "User",
                com.k12.platform.domain.model.UserRole.PARENT);

        PasswordResetToken mockToken = PasswordResetToken.create(mockUser.userId());

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(mockUser));
        when(passwordResetService.requestPasswordReset(any(UserId.class))).thenReturn(mockToken);

        String requestBody = "{\"email\":\"user@example.com\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/request")
                .then()
                .statusCode(is(200));
    }

    @Test
    @DisplayName("Should handle strong password")
    void shouldHandleStrongPassword() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("OldPassword123!"),
                "Test",
                "User",
                com.k12.platform.domain.model.UserRole.TEACHER);

        when(passwordResetService.validateResetToken(any(String.class))).thenReturn(mockUser.userId());
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));

        String requestBody = "{\"token\":\"test-token\",\"new_password\":\"VeryStrongPassword123!@#\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(200))
                .body("message", is("Password has been reset successfully"));
    }

    @Test
    @DisplayName("Should accept JSON content type")
    void shouldAcceptJsonContentType() {
        // Arrange
        User mockUser = User.register(
                EmailAddress.of("test@example.com"),
                PasswordHash.hash("OldPassword123!"),
                "Test",
                "User",
                com.k12.platform.domain.model.UserRole.TEACHER);

        PasswordResetToken mockToken = PasswordResetToken.create(mockUser.userId());

        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(mockUser));
        when(passwordResetService.requestPasswordReset(any(UserId.class))).thenReturn(mockToken);

        String requestBody = "{\"email\":\"test@example.com\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/request")
                .then()
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Should return 400 when token validation fails")
    void shouldReturn400WhenTokenValidationFails() {
        // Arrange
        when(passwordResetService.validateResetToken(any(String.class)))
                .thenThrow(new IllegalStateException("Token has already been used"));

        String requestBody = "{\"token\":\"used-token\",\"new_password\":\"NewSecure123!\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(400))
                .body("message", is("Token has already been used"));
    }

    @Test
    @DisplayName("Should return 400 when user not found during confirm")
    void shouldReturn400WhenUserNotFound() {
        // Arrange
        UserId mockUserId = UserId.of(UUID.randomUUID());

        when(passwordResetService.validateResetToken(any(String.class))).thenReturn(mockUserId);
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        String requestBody = "{\"token\":\"valid-token\",\"new_password\":\"NewSecure123!\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/password-reset/confirm")
                .then()
                .statusCode(is(400))
                .body("message", is("User not found"));
    }
}
