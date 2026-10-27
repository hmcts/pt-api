package uk.gov.hmcts.reform.pt.functional.testutils;

import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.pt.functional.config.Endpoints;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;

import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.ptApiS2sToken;
import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.systemUserIdamToken;
import static uk.gov.hmcts.reform.pt.functional.config.TestConstants.BASE_URL;

public class TestCaseCleanUp {

    public static void deleteCase(Long caseReference) {
        try {
            SerenityRest.given()
                .baseUri(BASE_URL)
                .header(TestConstants.AUTHORIZATION, "Bearer " + systemUserIdamToken)
                .header(TestConstants.SERVICE_AUTHORIZATION, ptApiS2sToken)
                .pathParam("caseReference", caseReference)
                .when()
                .delete(Endpoints.DeleteTestCase.getResource());
        } catch (Exception e) {
            System.err.println("Failed to delete case for caseReference=" + caseReference + " " + e.getMessage());
        }
    }
}
