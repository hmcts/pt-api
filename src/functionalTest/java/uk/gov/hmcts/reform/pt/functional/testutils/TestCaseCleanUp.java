package uk.gov.hmcts.reform.pt.functional.testutils;

import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.pt.functional.config.Endpoints;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;

import static uk.gov.hmcts.reform.pt.functional.steps.ApiSteps.citizenUserIdamToken;
import static uk.gov.hmcts.reform.pt.functional.steps.ApiSteps.ptApiS2sToken;

public class TestCaseCleanUp {

    private static final String baseUrl = System.getenv("TEST_URL");

    public static void deleteCase(Long caseReference) {
        try {
            SerenityRest.given()
                .baseUri(baseUrl)
                .header(TestConstants.AUTHORIZATION, "Bearer " + citizenUserIdamToken)
                .header(TestConstants.SERVICE_AUTHORIZATION, ptApiS2sToken)
                .pathParam("caseReference", caseReference)
                .when()
                .delete(Endpoints.DeleteTestCase.getResource());

        } catch (Exception e) {
            System.err.println("Failed to delete case for caseReference=" + caseReference + " " + e.getMessage());
        }
    }
}
