package uk.gov.hmcts.reform.pt.functional.tests;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.testutils.CcdCaseCreator;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.TestCaseCleanUp;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.citizenUserIdamToken;
import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.ptApiS2sToken;
import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.systemUserIdamToken;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
@EnabledIfEnvironmentVariable(named = "CCD_ENABLED", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RespondentApplicationsEndpointTests {

    @Steps
    ApiSteps apiSteps;

    private static Long caseId;
    private static String landlordToken;

    @BeforeAll
    static void createCaseAndLinkLandlord() {
        caseId = CcdCaseCreator.createCitizenCase(citizenUserIdamToken, ptApiS2sToken);

        CcdCaseCreator.updateCitizenCase(caseId, "citizen-update-application",
                                         Map.of("landlordDetails", Map.of(
                                             "representativeType", "noLettingAgentOrRepresentative",
                                             "landlordPartyDetails", Map.of(
                                                 "firstName", "Larry",
                                                 "lastName", "Landlord",
                                                 "emailAddress", "simulate-delivered@notifications.service.gov.uk"))),
                                         citizenUserIdamToken, ptApiS2sToken);

        ApiSteps steps = new ApiSteps();
        steps.requestIsPreparedWithAppropriateValues();
        steps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        steps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.systemUser);
        steps.theRequestContainsThePathParameter("caseReference", caseId.toString());
        steps.callIsSubmittedToTheEndpoint("IssueLandlordAccessCode", "POST");
        steps.checkStatusCode(200);

        landlordToken = PtIdamTokenClient.generateToken(PtIdamTokenClient.UserType.citizenUser);

        steps.requestIsPreparedWithAppropriateValues();
        steps.theRequestContainsValidServiceToken(TestConstants.PT_FRONTEND);
        steps.theRequestContainsIdamToken(landlordToken);
        steps.theRequestContainsThePathParameter("caseReference", caseId.toString());
        String accessCode = steps.theResponseBodyValue("accessCode");
        steps.theRequestContainsBody(Map.of("accessCode", accessCode));
        steps.callIsSubmittedToTheEndpoint("ValidateAccessCode", "POST");
        steps.checkStatusCode(200);
    }

    @AfterAll
    static void tearDown() {
        String landlordId = SerenityRest.given()
            .baseUri(System.getenv("IDAM_API_URL"))
            .header(TestConstants.AUTHORIZATION, "Bearer " + landlordToken)
            .get("/o/userinfo")
            .then().extract().path("uid");

        SerenityRest.given()
            .baseUri(System.getenv("DATA_STORE_URL_BASE"))
            .header(TestConstants.AUTHORIZATION, "Bearer " + systemUserIdamToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, ptApiS2sToken)
            .contentType(ContentType.JSON)
            .body(Map.of("case_users", List.of(Map.of(
                "case_id", caseId.toString(),
                "user_id", landlordId,
                "case_role", "[DEFENDANT]"))))
            .delete("/case-users");

        TestCaseCleanUp.deleteCase(caseId);
    }

    @Title("Respondent Applications Endpoint Tests - should not return a linked case in an excluded state")
    @Test
    void linkedCaseInExcludedStateIsNotReturned() {
        getRespondentApplications(landlordToken);
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyDoesNotContainCaseReference(caseId);
    }

    @Title("Respondent Applications Endpoint Tests - should not return the case to the applicant")
    @Test
    void applicantDoesNotSeeCaseAsRespondent() {
        getRespondentApplications(citizenUserIdamToken);
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyDoesNotContainCaseReference(caseId);
    }

    @Title("Applications Endpoint Tests - should not return a case the user is only linked to as respondent")
    @Test
    void respondentDoesNotSeeCaseAsApplicant() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsIdamToken(landlordToken);
        apiSteps.callIsSubmittedToTheEndpoint("Applications", "GET");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyDoesNotContainCaseReference(caseId);
    }

    @Title("Respondent Applications Endpoint Tests - should return 200 and empty list when user has no linked cases")
    @Test
    void userWithNoLinkedCasesGetsEmptyList() {
        getRespondentApplications(PtIdamTokenClient.generateToken(PtIdamTokenClient.UserType.citizenUser));
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyIsAnEmptyArray();
    }

    @Title("Respondent Applications Endpoint Tests - should return 401 when the S2S token is invalid")
    @Test
    void expiredServiceToken() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsExpiredServiceToken();
        apiSteps.theRequestContainsIdamToken(landlordToken);
        apiSteps.callIsSubmittedToTheEndpoint("RespondentApplications", "GET");
        apiSteps.checkStatusCode(401);
    }

    @Title("Respondent Applications Endpoint Tests - should return 403 when an unauthorised S2S token is used")
    @Test
    void unauthorisedServiceToken() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsUnauthorisedServiceToken();
        apiSteps.theRequestContainsIdamToken(landlordToken);
        apiSteps.callIsSubmittedToTheEndpoint("RespondentApplications", "GET");
        apiSteps.checkStatusCode(403);
    }

    private void getRespondentApplications(String userToken) {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsIdamToken(userToken);
        apiSteps.callIsSubmittedToTheEndpoint("RespondentApplications", "GET");
    }
}
