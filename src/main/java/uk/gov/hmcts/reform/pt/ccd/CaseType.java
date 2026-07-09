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
    private static final String CASE_TYPE_NAME = "Possession";
    private static final String CASE_TYPE_DESCRIPTION = "Possession Case Type";
    private static final String JURISDICTION_ID = "PT";
    private static final String JURISDICTION_NAME = "Civil Possession";
    private static final String JURISDICTION_DESCRIPTION = "Civil Possession Jurisdiction";

    @Value("${caseApi.url}")
    private String caseApiUrl;

    public static String getCaseType() {
        return withSuffix(CASE_TYPE_ID, "-");
    }

    public static String getCaseTypeName() {
        return withSuffix(CASE_TYPE_NAME, " ");
    }

    private static String withSuffix(String base, String separator) {
        return ofNullable(getenv().get("CASE_TYPE_SUFFIX"))
            .map(changeId -> base + separator + changeId)
            .orElse(base);
    }

    @Override
    public void configure(final ConfigBuilder<PTCase, State, UserRole> builder) {
        builder.setCallbackHost(caseApiUrl);

        builder.caseType(getCaseType(), getCaseTypeName(), CASE_TYPE_DESCRIPTION);
        builder.jurisdiction(JURISDICTION_ID, JURISDICTION_NAME, JURISDICTION_DESCRIPTION);

        var label = "Applicant First Name";
        builder.searchInputFields()
            .field(PTCase::getFirstName, label);
        builder.searchCasesFields()
            .field(PTCase::getFirstName, label);

        builder.searchResultFields()
            .field(PTCase::getFirstName, label);
        builder.workBasketInputFields()
            .field(PTCase::getFirstName, label);
        builder.workBasketResultFields()
            .field(PTCase::getFirstName, label);

        builder.tab("Example", "Example Tab")
            .field(PTCase::getFirstName);
    }
}
