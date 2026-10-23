package uk.gov.hmcts.reform.pt.functional.testutils;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;

import java.util.Map;

import static uk.gov.hmcts.reform.pt.functional.testutils.EnvUtils.getEnv;

public class CcdCaseCreator {

    private static final String DATA_STORE_URL = getEnv("DATA_STORE_URL_BASE");
    private static final String CREATE_EVENT = "citizen-create-application";

    private CcdCaseCreator() {
    }

    public static Long createCitizenCase(String citizenToken, String s2sToken) {
        String caseType = CaseType.getCaseType();

        String eventToken = SerenityRest.given()
            .baseUri(DATA_STORE_URL)
            .header(TestConstants.AUTHORIZATION, "Bearer " + citizenToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, s2sToken)
            .header("experimental", "true")
            .get("/case-types/{caseType}/event-triggers/{eventId}", caseType, CREATE_EVENT)
            .then().log().ifValidationFails().statusCode(200)
            .extract().path("token");

        Map<String, Object> body = Map.of(
            "data", Map.of(
                "applicantFirstName", "Test",
                "applicantLastName", "Applicant",
                "email", "test.applicant@example.com",
                "postcode", "BH1 3BP",
                "applicationType", "challengeRentIncrease",
                "tenancyType", "assuredPeriodicTenancy"
            ),
            "event", Map.of("id", CREATE_EVENT),
            "event_token", eventToken
        );

        Object id = SerenityRest.given()
            .baseUri(DATA_STORE_URL)
            .header(TestConstants.AUTHORIZATION, "Bearer " + citizenToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, s2sToken)
            .header("experimental", "true")
            .contentType(ContentType.JSON)
            .body(body)
            .post("/case-types/{caseType}/cases", caseType)
            .then().log().ifValidationFails().statusCode(201)
            .extract().path("id");

        return Long.valueOf(id.toString());
    }

    public static void updateCitizenCase(Long caseId, String eventId, Map<String, Object> data,
                                         String citizenToken, String s2sToken) {
        String eventToken = SerenityRest.given()
            .baseUri(DATA_STORE_URL)
            .header(TestConstants.AUTHORIZATION, "Bearer " + citizenToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, s2sToken)
            .header("experimental", "true")
            .get("/cases/{caseId}/event-triggers/{eventId}", caseId, eventId)
            .then().log().ifValidationFails().statusCode(200)
            .extract().path("token");

        SerenityRest.given()
            .baseUri(DATA_STORE_URL)
            .header(TestConstants.AUTHORIZATION, "Bearer " + citizenToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, s2sToken)
            .header("experimental", "true")
            .contentType(ContentType.JSON)
            .body(Map.of("data", data, "event", Map.of("id", eventId), "event_token", eventToken))
            .post("/cases/{caseId}/events", caseId)
            .then().log().ifValidationFails().statusCode(201);
    }
}
