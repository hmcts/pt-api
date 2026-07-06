package uk.gov.hmcts.reform.pt.functional.steps;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.annotations.Step;
import static org.hamcrest.Matchers.equalTo;

public class ApiSteps {

    private static final String baseUrl = System.getenv("TEST_URL");

    @Step("Check Health")
    public void getHealth() {
        SerenityRest.given()
            .baseUri(baseUrl)
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
