package uk.gov.hmcts.reform.pt.functional.config;

import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.ServiceAuthenticationGenerator;

import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.caseworkerUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.citizenUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.systemUser;

public final class AuthTokens {

    public static final String ptApiS2sToken;
    public static final String ptFrontendS2sToken;
    public static final String unauthorisedS2sToken;
    public static final String systemUserIdamToken;
    public static final String citizenUserIdamToken;
    public static final String caseworkerUserIdamToken;

    static {
        ServiceAuthenticationGenerator serviceAuthenticationGenerator = new ServiceAuthenticationGenerator();
        ptApiS2sToken = serviceAuthenticationGenerator.generate();
        ptFrontendS2sToken = serviceAuthenticationGenerator.generate(TestConstants.PT_FRONTEND);
        unauthorisedS2sToken = serviceAuthenticationGenerator.generate(TestConstants.CIVIL_SERVICE);
        systemUserIdamToken = PtIdamTokenClient.generateToken(systemUser);
        citizenUserIdamToken = PtIdamTokenClient.generateToken(citizenUser);
        caseworkerUserIdamToken = PtIdamTokenClient.generateToken(caseworkerUser);
    }

    private AuthTokens() {
    }
}
