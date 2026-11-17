package uk.gov.hmcts.reform.pt;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SmokeTest {
    @BeforeEach
    void setUp() {
        String testUrl = System.getenv("TEST_URL");

        if (testUrl == null || testUrl.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: TEST_URL");
        }

        RestAssured.baseURI = testUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Health endpoint reports up")
    void healthEndpointReportsUp() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("Endpoint rejects request without service token")
    void protectedEndpointRejectsRequestWithoutServiceToken() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/applications")
            .then()
            .statusCode(401);
    }
}
