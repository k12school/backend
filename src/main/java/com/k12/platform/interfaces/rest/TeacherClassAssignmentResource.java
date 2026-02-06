package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.TeacherClassAssignment;
import com.k12.platform.domain.model.TeacherClassAssignmentService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaTeacherClassAssignmentAdapter;
import com.k12.platform.interfaces.rest.dto.CreateTeacherClassAssignmentRequest;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.TeacherClassAssignmentResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * REST resource for Teacher-Class assignment management.
 */
@Path("/api/teacher-class-assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class TeacherClassAssignmentResource {

    private final JpaTeacherClassAssignmentAdapter repository;
    private final TeacherClassAssignmentService service;

    @POST
    @Operation(summary = "Assign teacher to class", description = "Creates a new teacher-class assignment")
    public Response createAssignment(@Valid CreateTeacherClassAssignmentRequest request) {
        try {
            UserId teacherId = UserId.of(request.teacherId());
            ClassId classId = ClassId.of(request.classId());
            TeacherRole role = TeacherRole.of(request.role());

            LocalDate assignedDate =
                    request.assignedDate() != null ? LocalDate.parse(request.assignedDate()) : LocalDate.now();

            TeacherClassAssignment assignment = service.assign(teacherId, classId, role, assignedDate);

            TeacherClassAssignmentResponse response = toResponse(assignment);
            return Response.created(URI.create("/api/teacher-class-assignments/"
                            + assignment.assignmentId().value()))
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
    @Path("/teacher/{teacherId}")
    @Operation(summary = "Get assignments by teacher ID")
    public Response getAssignmentsByTeacher(@PathParam("teacherId") String teacherId) {
        try {
            UserId teacherUserId = UserId.of(teacherId);
            List<TeacherClassAssignment> assignments = repository.findByTeacherId(teacherUserId);

            var responses = assignments.stream().map(this::toResponse).toList();

            return Response.ok(responses).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/class/{classId}")
    @Operation(summary = "Get assignments by class ID")
    public Response getAssignmentsByClass(@PathParam("classId") String classId) {
        try {
            ClassId classUuid = ClassId.of(classId);
            List<TeacherClassAssignment> assignments = repository.findByClassId(classUuid);

            var responses = assignments.stream().map(this::toResponse).toList();

            return Response.ok(responses).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    private TeacherClassAssignmentResponse toResponse(TeacherClassAssignment assignment) {
        return new TeacherClassAssignmentResponse(
                assignment.assignmentId().value().toString(),
                assignment.teacherId().value().toString(),
                assignment.classId().value().toString(),
                assignment.role().value(),
                assignment.assignedDate().toString());
    }
}
