package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.ParentStudentAssociation;
import com.k12.platform.domain.model.ParentStudentAssociationService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaParentStudentAssociationAdapter;
import com.k12.platform.interfaces.rest.dto.CreateParentStudentAssociationRequest;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.ParentStudentAssociationResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * REST resource for Parent-Student association management.
 */
@Path("/api/parent-student-associations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParentStudentAssociationResource {

    @Inject
    JpaParentStudentAssociationAdapter repository;

    @Inject
    ParentStudentAssociationService service;

    @POST
    @Operation(summary = "Associate parent with student", description = "Creates a new parent-student association")
    public Response createAssociation(@Valid CreateParentStudentAssociationRequest request) {
        try {
            UserId parentId = UserId.of(request.parentId());
            StudentId studentId = StudentId.of(request.studentId());
            RelationshipType relationshipType = RelationshipType.of(request.relationshipType());

            ParentStudentAssociation association =
                    service.associate(parentId, studentId, relationshipType, request.isPrimaryContact());

            ParentStudentAssociationResponse response = toResponse(association);
            return Response.created(URI.create("/api/parent-student-associations/"
                            + association.associationId().value()))
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
    @Path("/parent/{parentId}")
    @Operation(summary = "Get associations by parent ID")
    public Response getAssociationsByParent(@PathParam("parentId") String parentId) {
        try {
            UserId parentUserId = UserId.of(parentId);
            List<ParentStudentAssociation> associations = repository.findByParentId(parentUserId);

            var responses = associations.stream().map(this::toResponse).toList();

            return Response.ok(responses).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/student/{studentId}")
    @Operation(summary = "Get associations by student ID")
    public Response getAssociationsByStudent(@PathParam("studentId") String studentId) {
        try {
            StudentId id = StudentId.of(studentId);
            List<ParentStudentAssociation> associations = repository.findByStudentId(id);

            var responses = associations.stream().map(this::toResponse).toList();

            return Response.ok(responses).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    private ParentStudentAssociationResponse toResponse(ParentStudentAssociation association) {
        return new ParentStudentAssociationResponse(
                association.associationId().value().toString(),
                association.parentId().value().toString(),
                association.studentId().value().toString(),
                association.relationshipType().value(),
                association.isPrimaryContact());
    }
}
