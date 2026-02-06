package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.commands.LoginCommand;
import com.k12.platform.domain.model.exceptions.AccountDeactivatedException;
import com.k12.platform.domain.model.exceptions.DomainException;
import com.k12.platform.domain.model.exceptions.InvalidCredentialsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.service.AuthenticationService;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.LoginRequest;
import com.k12.platform.interfaces.rest.dto.LoginResponse;
import com.k12.platform.interfaces.rest.dto.UserResponse;
import com.k12.platform.interfaces.rest.jwt.TokenService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

/**
 * REST resource for authentication.
 * Thin layer: translates HTTP → domain commands, delegates to services.
 */
@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthResource {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        try {
            // Validate input
            if (request.email() == null || request.email().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Email is required"))
                        .build();
            }

            if (request.password() == null || request.password().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Password is required"))
                        .build();
            }

            // Validate email format
            EmailAddress email;
            try {
                email = EmailAddress.of(request.email());
            } catch (DomainException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid email format"))
                        .build();
            }

            // Map HTTP request → domain command
            var command = new LoginCommand(email, request.password());

            // Delegate to domain service
            User user = authenticationService.login(command);

            // Generate JWT token
            String token = tokenService.generateToken(user);

            // Map domain → HTTP response
            var userResponse = new UserResponse(
                    user.userId().toString(),
                    user.email().value(),
                    user.role().name(),
                    user.firstName(),
                    user.lastName(),
                    user.isActive());

            var loginResponse = new LoginResponse(token, userResponse);

            return Response.ok(loginResponse).build();

        } catch (InvalidCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Invalid credentials"))
                    .build();

        } catch (AccountDeactivatedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Account is deactivated"))
                    .build();
        }
    }
}
