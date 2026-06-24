package uk.gov.hmcts.reform.pt.ccd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;

/**
 * Setup some common possessions case type configuration.
 */
@Component
public class CaseType implements CCDConfig<PTCase, State, UserRole> {

    private static final String CASE_TYPE_ID = "PT";

    @Value("${caseApi.url}")
    private String caseApiUrl;

    public static String getCaseType() {
        return withSuffix(CASE_TYPE_ID, "-");
    }

    private static String withSuffix(String base, String separator) {
        return ofNullable(getenv().get("CASE_TYPE_SUFFIX"))
            .map(changeId -> base + separator + changeId)
            .orElse(base);
    }

    @Override
    public void configure(final ConfigBuilder<PTCase, State, UserRole> builder) {
        builder.setCallbackHost(caseApiUrl);

        builder.caseType("PT", "Civil Possessions", "Possessions");
        builder.jurisdiction("CIVIL", "Civil Possessions", "The new one");

        var label = "Applicant Forename";
        builder.searchInputFields()
            .field(PTCase::getApplicantForename, label);
        builder.searchCasesFields()
            .field(PTCase::getApplicantForename, label);

        builder.searchResultFields()
            .field(PTCase::getApplicantForename, label);
        builder.workBasketInputFields()
            .field(PTCase::getApplicantForename, label);
        builder.workBasketResultFields()
            .field(PTCase::getApplicantForename, label);

        builder.tab("Example", "Example Tab")
            .field(PTCase::getApplicantForename);
    }
}
