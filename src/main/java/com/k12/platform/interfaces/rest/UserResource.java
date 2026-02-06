package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.User;
import com.k12.platform.domain.model.UserRole;
import com.k12.platform.domain.model.commands.RegisterUserCommand;
import com.k12.platform.domain.model.exceptions.InvalidEmailException;
import com.k12.platform.domain.model.exceptions.UserAlreadyExistsException;
import com.k12.platform.domain.model.valueobjects.EmailAddress;
import com.k12.platform.domain.model.valueobjects.PasswordHash;
import com.k12.platform.domain.service.PasswordValidator;
import com.k12.platform.domain.service.UserRegistrationService;
import com.k12.platform.interfaces.rest.dto.CreateUserRequest;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.UserResponse;
import com.k12.platform.interfaces.rest.security.RequireRole;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

/**
 * REST resource for user management.
 * Thin layer: translates HTTP → domain commands, delegates to services.
 */
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {

    private final UserRegistrationService userRegistrationService;

    @POST
    @Path("/teacher")
    @RequireRole(value = com.k12.platform.interfaces.rest.security.UserRole.ADMIN)
    public Response createTeacher(CreateUserRequest request) {
        return createUser(request, UserRole.TEACHER);
    }

    @POST
    @Path("/parent")
    @RequireRole(value = com.k12.platform.interfaces.rest.security.UserRole.ADMIN)
    public Response createParent(@Valid CreateUserRequest request) {
        return createUser(request, UserRole.PARENT);
    }

    @POST
    @Path("/admin")
    @RequireRole(value = com.k12.platform.interfaces.rest.security.UserRole.ADMIN)
    public Response createAdmin(@Valid CreateUserRequest request) {
        return createUser(request, UserRole.ADMIN);
    }

    private Response createUser(CreateUserRequest request, UserRole role) {
        try {
            // Validate required fields
            if (request.email() == null || request.email().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: email"))
                        .build();
            }

            if (request.password() == null || request.password().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: password"))
                        .build();
            }

            if (request.firstName() == null || request.firstName().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: firstName"))
                        .build();
            }

            if (request.lastName() == null || request.lastName().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: lastName"))
                        .build();
            }

            // Validate email format
            EmailAddress email;
            try {
                email = EmailAddress.of(request.email());
            } catch (InvalidEmailException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid email format"))
                        .build();
            }

            // Validate password strength
            try {
                PasswordValidator.validate(request.password());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(e.getMessage()))
                        .build();
            }

            // Hash password
            PasswordHash passwordHash = PasswordHash.hash(request.password());

            // Map HTTP request → domain command
            var command =
                    new RegisterUserCommand(email, passwordHash, request.firstName(), request.lastName(), role);

            // Delegate to domain service
            User user = userRegistrationService.register(command);

            // Map domain → HTTP response
            var userResponse = new UserResponse(
                    user.userId().toString(),
                    user.email().value(),
                    user.role().name(),
                    user.firstName(),
                    user.lastName(),
                    user.isActive());

            return Response.status(Response.Status.CREATED).entity(userResponse).build();

        } catch (UserAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("User with this email already exists"))
                    .build();
        }
    }
}
