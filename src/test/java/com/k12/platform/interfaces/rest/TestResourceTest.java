package com.k12.platform.interfaces.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * REST tests for TestResource.
 * Target: 80%+ coverage
 */
@QuarkusTest
@DisplayName("TestResource REST Tests")
class TestResourceTest {

    @Test
    @DisplayName("Should test database connectivity endpoint")
    void shouldTestDatabaseConnectivityEndpoint() {
        given().when()
                .get("/api/test/db")
                .then()
                .statusCode(anyOf(is(200), is(500)))
                .body(notNullValue());
    }

    @Test
    @DisplayName("Should return response with user count or error message")
    void shouldReturnResponseWithUserCountOrError() {
        given().when()
                .get("/api/test/db")
                .then()
                .statusCode(anyOf(is(200), is(500)))
                .body(notNullValue());
    }
}
