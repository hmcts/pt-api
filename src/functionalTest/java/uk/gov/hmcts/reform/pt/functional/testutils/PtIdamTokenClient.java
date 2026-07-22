package uk.gov.hmcts.reform.pt.functional.testutils;

import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static uk.gov.hmcts.reform.pt.functional.config.AuthConfig.CLIENT_ID;
import static uk.gov.hmcts.reform.pt.functional.config.AuthConfig.SCOPE;
import static uk.gov.hmcts.reform.pt.functional.config.AuthConfig.GRANT_TYPE;
import static uk.gov.hmcts.reform.pt.functional.config.AuthConfig.ENDPOINT;
import static uk.gov.hmcts.reform.pt.functional.testutils.EnvUtils.getEnv;

public class PtIdamTokenClient {

    private static final String BASE_URL = getEnv("IDAM_API_URL");
    private static final String IDAM_SYSTEM_USERNAME = getEnv("IDAM_SYSTEM_USERNAME");
    private static final String IDAM_SYSTEM_PASSWORD = getEnv("IDAM_SYSTEM_USER_PASSWORD");
    private static final String GENERIC_PASSWORD = TestConstants.GENERIC_PASSWORD;
    private static final String CASEWORKER_USERNAME = TestConstants.PT_CASEWORKER_USER;
    private static final String CLIENT_SECRET = getEnv("PT_API_IDAM_SECRET");

    public enum UserType {
        systemUser,
        citizenUser,
        caseworkerUser
    }

    public static String generateToken(UserType user) {

        String username = switch (user) {
            case systemUser -> IDAM_SYSTEM_USERNAME;
            case citizenUser -> CitizenUserGenerator.createCitizenUser();
            case caseworkerUser -> CASEWORKER_USERNAME;
        };

        String password = switch (user) {
            case systemUser -> IDAM_SYSTEM_PASSWORD;
            case citizenUser, caseworkerUser -> GENERIC_PASSWORD;
        };

        Map<String, String> formData = Map.of(
            "username", username,
            "password", password,
            "client_id", CLIENT_ID,
            "client_secret", CLIENT_SECRET,
            "scope", SCOPE,
            "grant_type", GRANT_TYPE
        );

        SerenityRest
            .given()
            .baseUri(BASE_URL)
            .contentType(APPLICATION_FORM_URLENCODED_VALUE)
            .formParams(formData)
            .post(ENDPOINT);

        if (SerenityRest.lastResponse().statusCode() != 200) {
            throw new RuntimeException(String.format(
                "Failed to generate IDAM token. Status code: %d%nResponse: %s",
                SerenityRest.lastResponse().statusCode(),
                SerenityRest.lastResponse().prettyPrint()
            ));
        }

        assertThat(SerenityRest.lastResponse().getStatusCode()).isEqualTo(200);

        return SerenityRest.lastResponse().jsonPath().getString("access_token");
    }
}
