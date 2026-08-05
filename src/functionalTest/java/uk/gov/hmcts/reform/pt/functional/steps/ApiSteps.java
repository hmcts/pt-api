package uk.gov.hmcts.reform.pt.functional.steps;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import uk.gov.hmcts.reform.pt.functional.config.Endpoints;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.ServiceAuthenticationGenerator;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.annotations.Step;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.citizenUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.systemUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.caseworkerUser;

public class ApiSteps {

    private RequestSpecification request;
    private Response response;
    private static final String baseUrl = System.getenv("TEST_URL");
    public static String ptApiS2sToken;
    private static String ptFrontendS2sToken;
    private static String unauthorisedS2sToken;
    public static String systemUserIdamToken;
    public static String citizenUserIdamToken;
    public static String caseworkerUserIdamToken;

    @Step("Generate S2S tokens")
    public static void setUp() {
        ServiceAuthenticationGenerator serviceAuthenticationGenerator = new ServiceAuthenticationGenerator();
        ptApiS2sToken = serviceAuthenticationGenerator.generate();
        ptFrontendS2sToken = serviceAuthenticationGenerator.generate(TestConstants.PT_FRONTEND);
        unauthorisedS2sToken = serviceAuthenticationGenerator.generate(TestConstants.CIVIL_SERVICE);

        systemUserIdamToken = PtIdamTokenClient.generateToken(systemUser);
        citizenUserIdamToken = PtIdamTokenClient.generateToken(citizenUser);
        caseworkerUserIdamToken = PtIdamTokenClient.generateToken(caseworkerUser);

        SerenityRest.given().baseUri(baseUrl);
    }

    @Step("a request is prepared with appropriate values")
    public void requestIsPreparedWithAppropriateValues() {
        request = SerenityRest.given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON);
    }

    @Step("the request contains a valid service token for {0}")
    public void theRequestContainsValidServiceToken(String microservice) {
        final Map<String, String> serviceTokens = Map.of(
            TestConstants.PT_API, ptApiS2sToken,
            TestConstants.PT_FRONTEND, ptFrontendS2sToken
        );

        if (!serviceTokens.containsKey(microservice.toLowerCase())) {
            throw new IllegalArgumentException("Unknown microservice: " + microservice);
        }

        String validS2sToken = serviceTokens.get(microservice.toLowerCase());
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, validS2sToken);
    }

    @Step("the request contains a valid IDAM token")
    public void theRequestContainsValidIdamToken(PtIdamTokenClient.UserType user) {
        String userToken = switch (user) {
            case systemUser -> systemUserIdamToken;
            case citizenUser -> citizenUserIdamToken;
            case caseworkerUser -> caseworkerUserIdamToken;
        };

        request = request.header(TestConstants.AUTHORIZATION, "Bearer " + userToken);
    }

    @Step("the request contains an unauthorised service token")
    public void theRequestContainsUnauthorisedServiceToken() {
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, unauthorisedS2sToken);
    }

    @Step("the request contains an expired service token")
    public void theRequestContainsExpiredServiceToken() {
        String expiredS2sToken = TestConstants.EXPIRED_S2S_TOKEN;
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, expiredS2sToken);
    }

    @Step("a call is submitted to the {0} endpoint using a {1} request")
    public void callIsSubmittedToTheEndpoint(String resource, String method) {
        Endpoints resourceAPI = Endpoints.valueOf(resource);

        response = switch (method.toUpperCase()) {
            case "POST" -> request.when().post(resourceAPI.getResource());
            case "GET" -> request.when().get(resourceAPI.getResource());
            case "DELETE" -> request.when().delete(resourceAPI.getResource());
            case "PUT" -> request.when().put(resourceAPI.getResource());
            default -> throw new IllegalStateException("Unexpected value: " + method.toUpperCase());
        };
    }

    @Step("Check status code is {0}")
    public void checkStatusCode(int statusCode) {
        if (response == null) {
            throw new IllegalStateException("No response available. Did you call callIsSubmittedToTheEndpoint first?");
        }
        response.then().assertThat().statusCode(statusCode);
    }

    @Step("the response body is an empty array")
    public void theResponseBodyIsAnEmptyArray() {
        SerenityRest.then()
            .assertThat()
            .body("", Matchers.hasSize(0));
    }

    @Step("the request contains an expired IDAM token")
    public void theRequestContainsExpiredIdamToken() {
        String expiredIdamToken = TestConstants.EXPIRED_IDAM_TOKEN;
        request = request.header(TestConstants.AUTHORIZATION, "Bearer " + expiredIdamToken);
    }

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
