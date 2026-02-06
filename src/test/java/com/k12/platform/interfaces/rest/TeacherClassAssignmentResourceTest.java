package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.TeacherClassAssignment;
import com.k12.platform.domain.model.TeacherClassAssignmentService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaTeacherClassAssignmentAdapter;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for TeacherClassAssignmentResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("TeacherClassAssignmentResource REST Tests")
class TeacherClassAssignmentResourceTest {

    @InjectMock
    TeacherClassAssignmentService service;

    @InjectMock
    JpaTeacherClassAssignmentAdapter repository;

    @Test
    @DisplayName("Should create assignment successfully")
    void shouldCreateAssignmentEndpointExists() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId),
                ClassId.of(classId),
                TeacherRole.of("Homeroom Teacher"),
                LocalDate.of(2024, 9, 1));

        when(service.assign(any(UserId.class), any(ClassId.class), any(TeacherRole.class), any(LocalDate.class)))
                .thenReturn(mockAssignment);

        String requestBody = "{\"teacher_id\":\"" + teacherId + "\",\"class_id\":\"" + classId
                + "\",\"role\":\"Homeroom Teacher\",\"assigned_date\":\"2024-09-01\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(201))
                .body("role", is("Homeroom Teacher"));
    }

    @Test
    @DisplayName("Should get assignments by teacher ID successfully")
    void shouldGetAssignmentsByTeacherId() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId), ClassId.of(classId), TeacherRole.of("Homeroom Teacher"), LocalDate.now());

        when(repository.findByTeacherId(any(UserId.class))).thenReturn(List.of(mockAssignment));

        // Act & Assert
        given().when()
                .get("/api/teacher-class-assignments/teacher/" + teacherId)
                .then()
                .statusCode(is(200));
    }

    @Test
    @DisplayName("Should return 400 for invalid teacher ID")
    void shouldReturn400ForInvalidTeacherId() {
        given().when()
                .get("/api/teacher-class-assignments/teacher/invalid-uuid")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid UUID string: invalid-uuid"));
    }

    @Test
    @DisplayName("Should handle different teacher roles")
    void shouldHandleDifferentRoles() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId), ClassId.of(classId), TeacherRole.of("Subject Teacher"), LocalDate.of(2024, 9, 1));

        when(service.assign(any(UserId.class), any(ClassId.class), any(TeacherRole.class), any(LocalDate.class)))
                .thenReturn(mockAssignment);

        String requestBody = "{\"teacher_id\":\"" + teacherId + "\",\"class_id\":\"" + classId
                + "\",\"role\":\"Subject Teacher\",\"assigned_date\":\"2024-09-01\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(201))
                .body("role", is("Subject Teacher"));
    }

    @Test
    @DisplayName("Should handle assignment without date")
    void shouldHandleAssignmentWithoutDate() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId), ClassId.of(classId), TeacherRole.of("Assistant Teacher"), LocalDate.now());

        when(service.assign(any(UserId.class), any(ClassId.class), any(TeacherRole.class), any(LocalDate.class)))
                .thenReturn(mockAssignment);

        String requestBody = "{\"teacher_id\":\"" + teacherId + "\",\"class_id\":\"" + classId
                + "\",\"role\":\"Assistant Teacher\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(201))
                .body("role", is("Assistant Teacher"));
    }

    @Test
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields() {
        String requestBody = "{}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should handle various assigned dates")
    void shouldHandleVariousDates() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId),
                ClassId.of(classId),
                TeacherRole.of("Homeroom Teacher"),
                LocalDate.of(2024, 1, 15));

        when(service.assign(any(UserId.class), any(ClassId.class), any(TeacherRole.class), any(LocalDate.class)))
                .thenReturn(mockAssignment);

        String requestBody = "{\"teacher_id\":\"" + teacherId + "\",\"class_id\":\"" + classId
                + "\",\"role\":\"Homeroom Teacher\",\"assigned_date\":\"2024-01-15\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(201))
                .body("assigned_date", is("2024-01-15"));
    }

    @Test
    @DisplayName("Should get assignments by class ID successfully")
    void shouldGetAssignmentsByClassId() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        TeacherClassAssignment mockAssignment = TeacherClassAssignment.create(
                UserId.of(teacherId), ClassId.of(classId), TeacherRole.of("Homeroom Teacher"), LocalDate.now());

        when(repository.findByClassId(any(ClassId.class))).thenReturn(List.of(mockAssignment));

        // Act & Assert
        given().when()
                .get("/api/teacher-class-assignments/class/" + classId)
                .then()
                .statusCode(is(200));
    }

    @Test
    @DisplayName("Should return 400 for invalid class ID")
    void shouldReturn400ForInvalidClassId() {
        given().when()
                .get("/api/teacher-class-assignments/class/invalid-uuid")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid UUID string: invalid-uuid"));
    }

    @Test
    @DisplayName("Should return 409 for duplicate assignment")
    void shouldReturn409ForDuplicateAssignment() {
        // Arrange
        UUID teacherId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();

        when(service.assign(any(UserId.class), any(ClassId.class), any(TeacherRole.class), any(LocalDate.class)))
                .thenThrow(new IllegalStateException("Assignment already exists"));

        String requestBody =
                "{\"teacher_id\":\"" + teacherId + "\",\"class_id\":\"" + classId + "\",\"role\":\"Homeroom Teacher\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/teacher-class-assignments")
                .then()
                .statusCode(is(409))
                .body("message", is("Assignment already exists"));
    }
}
