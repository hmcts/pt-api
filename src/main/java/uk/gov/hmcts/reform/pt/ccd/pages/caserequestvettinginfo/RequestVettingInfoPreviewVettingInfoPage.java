package uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo;

import uk.gov.hmcts.reform.pt.ccd.common.CcdPage;
import uk.gov.hmcts.reform.pt.ccd.common.CcdPageConfiguration;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;

import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.CCD_CASE_SUMMARY_HTML;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.LINE_SEPARATOR;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.NEVER_SHOW;

public class RequestVettingInfoPreviewVettingInfoPage extends CcdPage implements CcdPageConfiguration {
    private static final String PAGE_CONTENT =
        """
        <p>
          <a class="govuk-link" href="https://gov.uk" target="_blank">
            Preview vetting information request (open in new tab)</a> before you send it to the applicant.
        </p>
        """;

    @Override
    public void buildPage(PageBuilder pageBuilder) {
        pageBuilder
            .page(getPageKey())
            .readonly(PTCase::getFormattedPropertyAddress, NEVER_SHOW)
            .readonly(PTCase::getFormattedCaseName, NEVER_SHOW)
            .pageLabel("Preview vetting information request")
            .label(deriveSummaryKey(), CCD_CASE_SUMMARY_HTML)
            .label(deriveLineSeparatorKey(), LINE_SEPARATOR)
            .label(deriveKey("content"), PAGE_CONTENT);
    }
}
