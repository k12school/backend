package com.k12.platform.interfaces.rest;

import com.k12.platform.domain.model.Student;
import com.k12.platform.domain.model.StudentRegistrationService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaStudentAdapter;
import com.k12.platform.interfaces.rest.dto.CreateStudentRequest;
import com.k12.platform.interfaces.rest.dto.ErrorResponse;
import com.k12.platform.interfaces.rest.dto.StudentResponse;
import com.k12.platform.interfaces.rest.dto.TransferGradeRequest;
import com.k12.platform.interfaces.rest.security.RequireRole;
import com.k12.platform.interfaces.rest.security.UserRole;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * REST resource for Student management.
 */
@Path("/api/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class StudentResource {

    private final JpaStudentAdapter studentRepository;
    private final StudentRegistrationService studentRegistrationService;

    @POST
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Register a new student", description = "Creates a new student record (Admin only)")
    @APIResponse(responseCode = "201", description = "Student created successfully")
    @APIResponse(responseCode = "400", description = "Invalid request data")
    @APIResponse(responseCode = "403", description = "Insufficient permissions")
    public Response createStudent(@Valid CreateStudentRequest request) {
        try {
            LocalDate enrollmentDate =
                    request.enrollmentDate() != null ? LocalDate.parse(request.enrollmentDate()) : LocalDate.now();

            StudentPersonalInfo personalInfo =
                    StudentPersonalInfo.of(request.firstName(), request.lastName(), request.dateOfBirth());

            GradeLevel gradeLevel = GradeLevel.fromString(request.gradeLevel());
            UserId classId = UserId.of(request.classId());
            StudentNumber studentNumber =
                    request.studentNumber() != null ? StudentNumber.of(request.studentNumber()) : StudentNumber.empty();

            Student student = studentRegistrationService.registerStudent(
                    personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

            StudentResponse response = toResponse(student);
            return Response.created(
                            URI.create("/api/students/" + student.studentId().value()))
                    .entity(response)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.TEACHER, UserRole.PARENT})
    @Operation(
            summary = "Get student by ID",
            description =
                    "Retrieves a student's information. Teachers can only access students in their classes. Parents can only access their linked children.")
    @APIResponse(responseCode = "200", description = "Student found")
    @APIResponse(responseCode = "403", description = "Insufficient permissions or not authorized for this student")
    @APIResponse(responseCode = "404", description = "Student not found")
    public Response getStudent(@PathParam("id") String id) {
        try {
            StudentId studentId = StudentId.of(id);
            Student student =
                    studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

            StudentResponse response = toResponse(student);
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid student ID format"))
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{id}/transfer")
    @RequireRole(UserRole.ADMIN)
    @Operation(
            summary = "Transfer student to different grade",
            description = "Transfers a student to a different grade level (Admin only)")
    @APIResponse(responseCode = "200", description = "Student transferred successfully")
    @APIResponse(responseCode = "403", description = "Insufficient permissions")
    @APIResponse(responseCode = "404", description = "Student not found")
    public Response transferGrade(@PathParam("id") String id, @Valid TransferGradeRequest request) {
        try {
            StudentId studentId = StudentId.of(id);
            Student student =
                    studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

            GradeLevel newGrade = GradeLevel.fromString(request.gradeLevel());
            student.transferGrade(newGrade);
            studentRepository.save(student);

            StudentResponse response = toResponse(student);
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{id}/advance")
    @RequireRole(UserRole.ADMIN)
    @Operation(
            summary = "Advance student to next grade",
            description = "Advances a student to the next grade level (Admin only)")
    @APIResponse(responseCode = "200", description = "Student advanced successfully")
    @APIResponse(responseCode = "403", description = "Insufficient permissions")
    @APIResponse(responseCode = "404", description = "Student not found")
    public Response advanceGrade(@PathParam("id") String id) {
        try {
            StudentId studentId = StudentId.of(id);
            Student student =
                    studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

            student.advanceGrade();
            studentRepository.save(student);

            StudentResponse response = toResponse(student);
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    private StudentResponse toResponse(Student student) {
        String studentNumber = student.studentNumber().isEmpty()
                ? null
                : student.studentNumber().value();
        return new StudentResponse(
                student.studentId().value().toString(),
                student.personalInfo().firstName(),
                student.personalInfo().lastName(),
                student.personalInfo().dateOfBirth().toString(),
                student.gradeLevel().displayValue(),
                student.classId().value().toString(),
                studentNumber,
                student.enrollmentDate().toString());
    }
}
