package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.k12.platform.domain.model.ParentStudentAssociation;
import com.k12.platform.domain.model.ParentStudentAssociationService;
import com.k12.platform.domain.model.valueobjects.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for ParentStudentAssociationResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("ParentStudentAssociationResource REST Tests")
class ParentStudentAssociationResourceTest {

    @InjectMock
    ParentStudentAssociationService service;

    @Test
    @DisplayName("Should create association successfully")
    void shouldCreateAssociationSuccessfully() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Father"), true);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Father\",\"is_primary_contact\":true}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(201))
                .body("relationship_type", is("Father"))
                .body("is_primary_contact", is(true));
    }

    @Test
    @DisplayName("Should create association as non-primary contact")
    void shouldCreateNonPrimaryAssociation() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Mother"), false);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Mother\",\"is_primary_contact\":false}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(201))
                .body("is_primary_contact", is(false));
    }

    @Test
    @DisplayName("Should return 400 for invalid parent ID")
    void shouldReturn400ForInvalidParentId() {
        UUID studentId = UUID.randomUUID();
        String requestBody = "{\"parent_id\":\"invalid-uuid\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Father\",\"is_primary_contact\":false}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid UUID string: invalid-uuid"));
    }

    @Test
    @DisplayName("Should return 400 for invalid student ID")
    void shouldReturn400ForInvalidStudentId() {
        UUID parentId = UUID.randomUUID();
        String requestBody = "{\"parent_id\":\"" + parentId
                + "\",\"student_id\":\"invalid-uuid\",\"relationship_type\":\"Father\",\"is_primary_contact\":false}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(400))
                .body("message", is("Invalid UUID string: invalid-uuid"));
    }

    @Test
    @DisplayName("Should handle different relationship types")
    void shouldHandleDifferentRelationshipTypes() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Guardian"), false);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String[] relationships = {"Father", "Mother", "Guardian", "Grandparent"};

        for (String relationship : relationships) {
            String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                    + "\",\"relationship_type\":\"" + relationship + "\",\"is_primary_contact\":false}";

            given().contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/parent-student-associations")
                    .then()
                    .statusCode(is(201));
        }
    }

    @Test
    @DisplayName("Should return 409 for duplicate association")
    void shouldReturn409ForDuplicate() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenThrow(new IllegalStateException("Association already exists"));

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Father\",\"is_primary_contact\":false}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(409))
                .body("message", is("Association already exists"));
    }

    @Test
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields() {
        String requestBody = "{}";

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(400));
    }

    @Test
    @DisplayName("Should handle guardian relationship")
    void shouldHandleGuardianRelationship() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Guardian"), true);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Guardian\",\"is_primary_contact\":true}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(201))
                .body("relationship_type", is("Guardian"));
    }

    @Test
    @DisplayName("Should handle primary contact flag")
    void shouldHandlePrimaryContactFlag() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Father"), true);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Father\",\"is_primary_contact\":true}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .statusCode(is(201))
                .body("is_primary_contact", is(true));
    }

    @Test
    @DisplayName("Should accept JSON content type")
    void shouldAcceptJsonContentType() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ParentStudentAssociation mockAssociation = ParentStudentAssociation.create(
                UserId.of(parentId), StudentId.of(studentId), RelationshipType.of("Father"), false);

        when(service.associate(
                        any(UserId.class), any(StudentId.class), any(RelationshipType.class), any(Boolean.class)))
                .thenReturn(mockAssociation);

        String requestBody = "{\"parent_id\":\"" + parentId + "\",\"student_id\":\"" + studentId
                + "\",\"relationship_type\":\"Father\",\"is_primary_contact\":false}";

        // Act & Assert
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/parent-student-associations")
                .then()
                .contentType(ContentType.JSON);
    }
}
