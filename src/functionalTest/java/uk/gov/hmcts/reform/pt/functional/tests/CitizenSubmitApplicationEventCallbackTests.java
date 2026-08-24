package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.steps.BaseApi;
import uk.gov.hmcts.reform.pt.functional.testutils.PayloadLoader;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.RandomNumberUtil;
import uk.gov.hmcts.reform.pt.functional.testutils.TestCaseCleanUp;

import java.util.Map;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
@EnabledIfEnvironmentVariable(named = "CCD_ENABLED", matches = "true")
class CitizenSubmitApplicationEventCallbackTests extends BaseApi {

    @Steps
    ApiSteps apiSteps;

    private static final Long caseId = RandomNumberUtil.generateRandomNumber(16);
    private static final String caseType = CaseType.getCaseType();

    @Title("citizenSubmitApplication submit event callback test - transitions a case and returns 200")
    @Test
    void citizenSubmitApplicationSubmitEventCallbackTest() {
        String createRequestBody = PayloadLoader.load(
            "/payloads/citizenCreateApplication-submitEventCallbackRequest.json",
            Map.of("caseTypeId", caseType, "caseId", caseId)
        );

        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
        apiSteps.theRequestContainsIdempotencyKeyHeader();
        apiSteps.theRequestContainsTheQueryParameter("eventId", "citizen-create-application");
        apiSteps.theRequestContainsBody(createRequestBody);
        apiSteps.callIsSubmittedToTheEndpoint("SubmitEventCallback", "POST");
        apiSteps.checkStatusCode(200);

        String submitRequestBody = PayloadLoader.load(
            "/payloads/citizenSubmitApplication-submitEventCallbackRequest.json",
            Map.of("caseTypeId", caseType, "caseId", caseId)
        );

        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
        apiSteps.theRequestContainsIdempotencyKeyHeader();
        apiSteps.theRequestContainsTheQueryParameter("eventId", "citizen-submit-application");
        apiSteps.theRequestContainsBody(submitRequestBody);
        apiSteps.callIsSubmittedToTheEndpoint("SubmitEventCallback", "POST");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyMatchesTheExpectedResponse(
            "/responses/citizenSubmitApplication-submitEventCallbackResponse.json");
    }

    @AfterAll
    static void tearDown() {
        TestCaseCleanUp.deleteCase(caseId);
    }
}
