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
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for user management.
 * Thin layer: translates HTTP → domain commands, delegates to services.
 */
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRegistrationService userRegistrationService;

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
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: email"))
                        .build();
            }

            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: password"))
                        .build();
            }

            if (request.getFirstName() == null || request.getFirstName().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: firstName"))
                        .build();
            }

            if (request.getLastName() == null || request.getLastName().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Missing required field: lastName"))
                        .build();
            }

            // Validate email format
            EmailAddress email;
            try {
                email = EmailAddress.of(request.getEmail());
            } catch (InvalidEmailException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid email format"))
                        .build();
            }

            // Validate password strength
            try {
                PasswordValidator.validate(request.getPassword());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(e.getMessage()))
                        .build();
            }

            // Hash password
            PasswordHash passwordHash = PasswordHash.hash(request.getPassword());

            // Map HTTP request → domain command
            var command =
                    new RegisterUserCommand(email, passwordHash, request.getFirstName(), request.getLastName(), role);

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
