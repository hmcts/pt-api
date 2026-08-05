package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.BaseApi;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
@EnabledIfEnvironmentVariable(named = "CCD_ENABLED", matches = "true")
class ApplicationsEndpointTests extends BaseApi {

    @Steps
    ApiSteps apiSteps;

    @Title("Applications Endpoint Tests - should return 200 when a user has no applications and return an empty list")
    @Test
    void applicationsEndpointTest200Scenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
        apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyIsAnEmptyArray();
    }

    @Title("Applications Endpoint Tests - should return 401 when the S2S token is missing")
    @Test
    void applicationsEndpointTest401MissingServiceTokenScenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
        apiSteps.checkStatusCode(401);
    }

    @Title("Applications Endpoint Tests - should return 401 when the S2S token is invalid")
    @Test
    void applicationsEndpointTest401InvalidServiceTokenScenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsExpiredServiceToken();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
        apiSteps.checkStatusCode(401);
    }

    @Title("Applications Endpoint Tests - should return 403 when an unauthorised S2S token is used")
    @Test
    void applicationsEndpointTest403Scenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsUnauthorisedServiceToken();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
        apiSteps.checkStatusCode(403);
    }
}
