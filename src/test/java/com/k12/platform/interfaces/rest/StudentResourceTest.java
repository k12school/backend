package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.Student;
import com.k12.platform.domain.model.StudentRegistrationService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaStudentAdapter;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for StudentResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("StudentResource REST Tests")
class StudentResourceTest {

    @InjectMock
    StudentRegistrationService studentRegistrationService;

    @InjectMock
    JpaStudentAdapter studentRepository;

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentEndpointExists() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("John", "Doe", "2010-05-15"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                java.time.LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"John\",\"last_name\":\"Doe\",\"date_of_birth\":\"2010-05-15\",\"grade_level\":\"5\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201))
                .body("first_name", is("John"))
                .body("last_name", is("Doe"))
                .body("grade_level", is("5"));
    }

    @Test
    @DisplayName("Should get student successfully")
    void shouldGetStudentEndpointExists() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Jane", "Smith", "2010-05-15"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.of(mockStudent));

        // Act & Assert
        given().when()
                .get("/api/students/" + studentId)
                .then()
                .statusCode(is(200))
                .body("first_name", is("Jane"))
                .body("last_name", is("Smith"));
    }

    @Test
    @DisplayName("Should return 404 when student not found")
    void shouldReturn404WhenStudentNotFound() {
        // Arrange
        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.empty());

        // Act & Assert
        given().when()
                .get("/api/students/" + UUID.randomUUID())
                .then()
                .statusCode(is(404))
                .body("message", is("Student not found"));
    }

    @Test
    @DisplayName("Should transfer grade successfully")
    void shouldTransferGradeEndpointExists() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("John", "Doe", "2010-05-15"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.of(mockStudent));

        String requestBody = "{\"grade_level\":\"6\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students/" + studentId + "/transfer")
                .then()
                .statusCode(is(200))
                .body("grade_level", is("6"));
    }

    @Test
    @DisplayName("Should advance grade successfully")
    void shouldAdvanceGradeEndpointExists() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("John", "Doe", "2010-05-15"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.of(mockStudent));

        // Act & Assert
        given().contentType(ContentType.JSON)
                .when()
                .post("/api/students/" + studentId + "/advance")
                .then()
                .statusCode(is(200))
                .body("grade_level", is("6"));
    }

    @Test
    @DisplayName("Should handle various grade levels")
    void shouldHandleVariousGradeLevels() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Test", "User", "2010-01-01"),
                GradeLevel.of(1),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Test\",\"last_name\":\"User\",\"date_of_birth\":\"2010-01-01\",\"grade_level\":\"1\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201))
                .body("grade_level", is("1"));
    }

    @Test
    @DisplayName("Should return 400 for invalid student ID format")
    void shouldValidateStudentIdFormat() {
        given().when()
                .get("/api/students/invalid-uuid")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid student ID format"));
    }

    @Test
    @DisplayName("Should handle JSON content type")
    void shouldHandleJsonContentType() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Test", "User", "2010-01-01"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Test\",\"last_name\":\"User\",\"date_of_birth\":\"2010-01-01\",\"grade_level\":\"5\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201));
    }

    @Test
    @DisplayName("Should handle kindergarten")
    void shouldHandleKindergarten() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Alice", "Johnson", "2018-08-15"),
                GradeLevel.of(0),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Alice\",\"last_name\":\"Johnson\",\"date_of_birth\":\"2018-08-15\",\"grade_level\":\"K\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201))
                .body("grade_level", is("K"));
    }

    @Test
    @DisplayName("Should handle grade 12")
    void shouldHandleGrade12() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Bob", "Jones", "2007-05-10"),
                GradeLevel.of(12),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Bob\",\"last_name\":\"Jones\",\"date_of_birth\":\"2007-05-10\",\"grade_level\":\"12\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201))
                .body("grade_level", is("12"));
    }

    @Test
    @DisplayName("Should handle missing optional fields")
    void shouldHandleMissingOptionalFields() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Jane", "Smith", "2011-06-20"),
                GradeLevel.of(4),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Jane\",\"last_name\":\"Smith\",\"date_of_birth\":\"2011-06-20\",\"grade_level\":\"4\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(201))
                .body("first_name", is("Jane"));
    }

    @Test
    @DisplayName("Should return 400 for invalid grade format")
    void shouldReturn400ForInvalidGradeFormat() {
        // Arrange
        UUID classId = UUID.randomUUID();
        Student mockStudent = Student.register(
                StudentPersonalInfo.of("Test", "User", "2010-01-01"),
                GradeLevel.of(5),
                UserId.of(classId),
                StudentNumber.empty(),
                LocalDate.now());

        when(studentRegistrationService.registerStudent(
                        any(StudentPersonalInfo.class),
                        any(GradeLevel.class),
                        any(UserId.class),
                        any(StudentNumber.class),
                        any(LocalDate.class)))
                .thenReturn(mockStudent);

        String requestBody =
                "{\"first_name\":\"Test\",\"last_name\":\"User\",\"date_of_birth\":\"2010-01-01\",\"grade_level\":\"invalid\",\"class_id\":\""
                        + classId + "\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/students")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should return 404 when advancing non-existent student")
    void shouldReturn404WhenAdvancingNonExistentStudent() {
        // Arrange
        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.empty());

        // Act & Assert
        given().contentType(ContentType.JSON)
                .when()
                .post("/api/students/" + UUID.randomUUID() + "/advance")
                .then()
                .statusCode(is(404))
                .body("message", is("Student not found"));
    }
}
