package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.BaseApi;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.testutils.PayloadLoader;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.RandomNumberUtil;
import uk.gov.hmcts.reform.pt.functional.testutils.TestCaseCleanUp;

import java.util.Map;

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

    @Title("Applications Endpoint Tests - should return 200 and the user's application once a case has been created")
    @Test
    void applicationsEndpointTest200WithApplicationScenario() {
        Long caseId = RandomNumberUtil.generateRandomNumber(16);
        try {
            String requestBody = PayloadLoader.load(
                "/payloads/citizenCreateApplication-submitEventCallbackRequest.json",
                Map.of("caseTypeId", CaseType.getCaseType(), "caseId", caseId)
            );

            apiSteps.requestIsPreparedWithAppropriateValues();
            apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
            apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
            apiSteps.theRequestContainsIdempotencyKeyHeader();
            apiSteps.theRequestContainsTheQueryParameter("eventId", "citizen-create-application");
            apiSteps.theRequestContainsBody(requestBody);
            apiSteps.callIsSubmittedToTheEndpoint("SubmitEventCallback", "POST");
            apiSteps.checkStatusCode(200);

            apiSteps.requestIsPreparedWithAppropriateValues();
            apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
            apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
            apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
            apiSteps.checkStatusCode(200);
            apiSteps.theResponseBodyMatchesTheExpectedResponse(
                PayloadLoader.load(
                    "/responses/getApplications-afterCitizenCreateApplicationResponse.json",
                    Map.of("caseId", caseId)
                ));
        } finally {
            TestCaseCleanUp.deleteCase(caseId);
        }
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
        apiSteps.theResponseBodyFieldEquals("message", "Invalid case reference: 1");
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
        apiSteps.theResponseBodyFieldEquals("message", "No case found with reference 1");
    }
}
