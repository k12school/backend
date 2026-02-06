package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.PasswordResetService;
import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.model.valueobjects.UserId;
import com.k12.platform.domain.port.UserRepository;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.PasswordResetConfirmRequest;
import com.k12.platform.interfaces.rest.dto.PasswordResetRequestRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * REST resource for Password Reset management.
 */
@Path("/api/auth/password-reset")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PasswordResetResource {

    @Inject
    PasswordResetService passwordResetService;

    @Inject
    UserRepository userRepository;

    @POST
    @Path("/request")
    @Operation(
            summary = "Request password reset",
            description = "Initiates password reset flow by sending reset token to user's email")
    public Response requestPasswordReset(@Valid PasswordResetRequestRequest request) {
        try {
            EmailAddress email = EmailAddress.of(request.email());
            User user =
                    userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

            UserId userId = user.userId();
            var resetToken = passwordResetService.requestPasswordReset(userId);

            // In a real system, you would send an email here with the token
            // For now, we return it in the response (not production-ready!)
            return Response.ok(new PasswordResetResponse(
                            "Password reset token generated. In production, this would be sent via email.",
                            resetToken.token().value()))
                    .build();

        } catch (IllegalArgumentException e) {
            // For security, don't reveal whether email exists
            return Response.ok(new PasswordResetResponse(
                            "If an account exists with this email, a password reset link will be sent.", null))
                    .build();
        }
    }

    @POST
    @Path("/confirm")
    @Operation(summary = "Confirm password reset", description = "Resets password using valid token")
    public Response confirmPasswordReset(@Valid PasswordResetConfirmRequest request) {
        try {
            // Validate token and get user ID
            UserId userId = passwordResetService.validateResetToken(request.token());

            // Get user
            User user =
                    userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Update password
            PasswordHash newPasswordHash = PasswordHash.hash(request.newPassword());
            user.updatePassword(newPasswordHash);
            userRepository.save(user);

            // Mark token as used
            passwordResetService.markTokenAsUsed(request.token());

            return Response.ok(new PasswordResetResponse("Password has been reset successfully", null))
                    .build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    record PasswordResetResponse(String message, String token) {}
}
