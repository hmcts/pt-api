package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Disabled;
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

    @Title("Applications Case Reference Endpoint Tests - should return 200 when the case exists")
    @Disabled("Unable to create case yet, so this will not return anything")
    @Test
    void applicationsCaseReferenceEndpointTest200Scenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1234123412341234");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyIsAnEmptyArray();
    }

    @Title("Applications Case Reference Endpoint Tests - should return 400 when the case ref is invalid")
    @Test
    void applicationsCaseReferenceEndpointTest400Scenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(400);
    }

    @Title("Applications Case Reference Endpoint Tests - should return 401 when the S2S token is missing")
    @Test
    void applicationsCaseReferenceEndpointTest401MissingServiceTokenScenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1234123412341234");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(401);
    }

    @Title("Applications Case Reference Endpoint Tests - should return 401 when the S2S token is invalid")
    @Test
    void applicationsCaseReferenceEndpointTest401InvalidServiceTokenScenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsExpiredServiceToken();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1234123412341234");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(401);
    }

    @Title("Applications Case Reference Endpoint Tests - should return 403 when an unauthorised S2S token is used")
    @Test
    void applicationsCaseReferenceEndpointTest403Scenario() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsUnauthorisedServiceToken();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1234123412341234");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(403);
    }

    @Title("Applications Case Reference Endpoint Tests - should return 404 when trying to return another user's case")
    @Disabled("Unable to create case yet, so this is not possible")
    @Test
    void applicationsCaseReferenceEndpointTest404Scenario() {
        // Create case by one user, then use different user's auth to try to access and should receive 404
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsUnauthorisedServiceToken();
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", "1234123412341234");
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(404);
    }
}
