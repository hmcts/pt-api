package uk.gov.hmcts.reform.pt.functional.config;

import static uk.gov.hmcts.reform.pt.functional.testutils.EnvUtils.getEnv;

public class TestConstants {
    public static final String PT_API = "pt_api";
    public static final String PT_FRONTEND = "pt_frontend";
    public static final String CIVIL_SERVICE = "civil_service";
    public static final String AUTHORIZATION = "Authorization";
    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String EXPIRED_S2S_TOKEN = getEnv("S2S_EXPIRED_TOKEN");
    public static final String EXPIRED_IDAM_TOKEN = getEnv("IDAM_EXPIRED_USER_TOKEN");
    public static final String PT_CASEWORKER_USER = "pt-caseworker-automation@test.com";
    public static final String PT_CASEWORKER_AUTOMATION_IDAM_UID = getEnv("PT_CASEWORKER_AUTOMATION_UID");
    public static final String GENERIC_PASSWORD = getEnv("IDAM_PT_USER_PASSWORD");
}
