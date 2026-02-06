package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.Class;
import com.k12.platform.domain.model.ClassService;
import com.k12.platform.domain.model.exceptions.DomainException;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaClassAdapter;
import com.k12.platform.interfaces.rest.dto.ClassResponse;
import com.k12.platform.interfaces.rest.dto.CreateClassRequest;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.security.RequireRole;
import com.k12.platform.interfaces.rest.security.UserRole;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * REST resource for Class management.
 */
@Path("/api/classes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ClassResource {

    private final JpaClassAdapter classRepository;
    private final ClassService classService;

    @POST
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Create a new class", description = "Creates a new school class (Admin only)")
    @APIResponse(responseCode = "201", description = "Class created successfully")
    @APIResponse(responseCode = "400", description = "Invalid request data")
    @APIResponse(responseCode = "403", description = "Insufficient permissions")
    @APIResponse(responseCode = "409", description = "Class already exists")
    public Response createClass(@Valid CreateClassRequest request) {
        try {
            ClassName name = ClassName.of(request.name());
            GradeLevel gradeLevel = GradeLevel.fromString(request.gradeLevel());
            AcademicYear academicYear = AcademicYear.of(request.academicYear());

            Class clazz = classService.createClass(name, gradeLevel, academicYear);

            ClassResponse response = toResponse(clazz);
            return Response.created(
                            URI.create("/api/classes/" + clazz.getClassId().value()))
                    .entity(response)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.TEACHER})
    @Operation(
            summary = "Get class by ID",
            description = "Retrieves a class's information. Teachers can only access their assigned classes.")
    @APIResponse(responseCode = "200", description = "Class found")
    @APIResponse(responseCode = "403", description = "Insufficient permissions or not authorized for this class")
    @APIResponse(responseCode = "404", description = "Class not found")
    public Response getClass(@PathParam("id") String id) {
        try {
            ClassId classId = ClassId.of(id);
            Class clazz = classRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found"));

            ClassResponse response = toResponse(clazz);
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid class ID format"))
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/grade/{grade}")
    @RequireRole({UserRole.ADMIN, UserRole.TEACHER})
    @Operation(summary = "Get classes by grade level", description = "Retrieves all classes for a specific grade")
    @APIResponse(responseCode = "200", description = "Classes found")
    @APIResponse(responseCode = "403", description = "Insufficient permissions")
    public Response getClassesByGrade(@PathParam("grade") String grade) {
        try {
            GradeLevel gradeLevel = GradeLevel.fromString(grade);
            var classes = classRepository.findByGradeLevel(gradeLevel);

            var responses = classes.stream().map(this::toResponse).toList();

            return Response.ok(responses).build();

        } catch (DomainException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    private ClassResponse toResponse(Class clazz) {
        return new ClassResponse(
                clazz.getClassId().value().toString(),
                clazz.getName().value(),
                clazz.getGradeLevel().displayValue(),
                clazz.getAcademicYear().value());
    }
}
