package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.Class;
import com.k12.platform.domain.model.ClassService;
import com.k12.platform.domain.model.valueobjects.*;
import com.k12.platform.infrastructure.persistence.JpaClassAdapter;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for ClassResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("ClassResource REST Tests")
class ClassResourceTest {

    @InjectMock
    ClassService classService;

    @InjectMock
    JpaClassAdapter classRepository;

    @Test
    @DisplayName("Should create class successfully")
    void shouldCreateClassEndpointExists() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("5-A"), GradeLevel.of(5), AcademicYear.of("2024-2025"));

        when(classService.createClass(any(ClassName.class), any(GradeLevel.class), any(AcademicYear.class)))
                .thenReturn(mockClass);

        String requestBody = "{\"name\":\"5-A\",\"grade_level\":\"5\",\"academic_year\":\"2024-2025\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(201))
                .body("name", is("5-A"))
                .body("grade_level", is("5"))
                .body("academic_year", is("2024-2025"));
    }

    @Test
    @DisplayName("Should get class by ID successfully")
    void shouldGetClassById() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("5-A"), GradeLevel.of(5), AcademicYear.of("2024-2025"));

        when(classRepository.findById(any(ClassId.class))).thenReturn(Optional.of(mockClass));

        // Act & Assert
        given().when()
                .get("/api/classes/" + UUID.randomUUID())
                .then()
                .statusCode(is(200))
                .body("name", is("5-A"))
                .body("grade_level", is("5"));
    }

    @Test
    @DisplayName("Should return 400 for invalid class ID format")
    void shouldReturn400ForInvalidClassId() {
        given().when()
                .get("/api/classes/invalid-uuid")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid class ID format"));
    }

    @Test
    @DisplayName("Should get classes by grade successfully")
    void shouldGetClassesByGrade() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("5-A"), GradeLevel.of(5), AcademicYear.of("2024-2025"));

        when(classRepository.findByGradeLevel(any(GradeLevel.class))).thenReturn(List.of(mockClass));

        // Act & Assert
        given().when().get("/api/classes/grade/5").then().statusCode(is(200));
    }

    @Test
    @DisplayName("Should return 500 for invalid grade format (BUG: should be 400)")
    void shouldReturn500ForInvalidGradeFormat() {
        given().when().get("/api/classes/grade/invalid").then().statusCode(is(400));
    }

    @Test
    @DisplayName("Should handle kindergarten class")
    void shouldHandleKindergartenClass() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("K-A"), GradeLevel.of(0), AcademicYear.of("2024-2025"));

        when(classService.createClass(any(ClassName.class), any(GradeLevel.class), any(AcademicYear.class)))
                .thenReturn(mockClass);

        String requestBody = "{\"name\":\"K-A\",\"grade_level\":\"K\",\"academic_year\":\"2024-2025\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(201))
                .body("name", is("K-A"))
                .body("grade_level", is("K"));
    }

    @Test
    @DisplayName("Should handle grade 12 class")
    void shouldHandleGrade12Class() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("12-A"), GradeLevel.of(12), AcademicYear.of("2024-2025"));

        when(classService.createClass(any(ClassName.class), any(GradeLevel.class), any(AcademicYear.class)))
                .thenReturn(mockClass);

        String requestBody = "{\"name\":\"12-A\",\"grade_level\":\"12\",\"academic_year\":\"2024-2025\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(201))
                .body("grade_level", is("12"));
    }

    @Test
    @DisplayName("Should handle various academic years")
    void shouldHandleVariousAcademicYears() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("5-A"), GradeLevel.of(5), AcademicYear.of("2025-2026"));

        when(classService.createClass(any(ClassName.class), any(GradeLevel.class), any(AcademicYear.class)))
                .thenReturn(mockClass);

        String requestBody = "{\"name\":\"5-A\",\"grade_level\":\"5\",\"academic_year\":\"2025-2026\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(201))
                .body("academic_year", is("2025-2026"));
    }

    @Test
    @DisplayName("Should handle different class names")
    void shouldHandleDifferentClassNames() {
        // Arrange
        Class mockClass = Class.create(ClassName.of("Room 101"), GradeLevel.of(5), AcademicYear.of("2024-2025"));

        when(classService.createClass(any(ClassName.class), any(GradeLevel.class), any(AcademicYear.class)))
                .thenReturn(mockClass);

        String requestBody = "{\"name\":\"Room 101\",\"grade_level\":\"5\",\"academic_year\":\"2024-2025\"}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(201))
                .body("name", is("Room 101"));
    }

    @Test
    @DisplayName("Should validate academic year format")
    void shouldValidateAcademicYearFormat() {
        String requestBody = "{\"name\":\"5-A\",\"grade_level\":\"5\",\"academic_year\":\"invalid\"}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/classes")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should return 404 when class not found")
    void shouldReturn404WhenClassNotFound() {
        // Arrange
        when(classRepository.findById(any(ClassId.class))).thenReturn(Optional.empty());

        // Act & Assert
        given().when()
                .get("/api/classes/" + UUID.randomUUID())
                .then()
                .statusCode(is(404))
                .body("message", is("Class not found"));
    }
}
