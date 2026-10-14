package uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo;

import uk.gov.hmcts.reform.pt.ccd.common.CcdPage;
import uk.gov.hmcts.reform.pt.ccd.common.CcdPageConfiguration;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;

import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.CCD_CASE_SUMMARY_HTML;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.HINT_SELECT_ALL_THAT_APPLY;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.LINE_SEPARATOR;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.NEVER_SHOW;

public class RequestVettingInfoWhatInfoNeededPage extends CcdPage implements CcdPageConfiguration {
    private static final String MAIN_QUESTION = "What information do you need to request from the applicant?";
    private static final String OTHER_QUESTION = "Enter the other documents or information you want to request";
    private static final String OTHER_QUESTION_HINT =
        "The text you enter here will appear on the email to the applicant. "
            + "You can preview the email on the next page.";
    private static final String OTHER_CONDITION = "vettingInfoRequested CONTAINS \"OTHER\"";

    @Override
    public void buildPage(PageBuilder pageBuilder) {
        pageBuilder
            .page(getPageKey())
            .complex(PTCase::getPropertyDetails, NEVER_SHOW)
                .readonly(PropertyDetails::getAddressLine1, NEVER_SHOW)
                .readonly(PropertyDetails::getAddressLine2, NEVER_SHOW)
                .readonly(PropertyDetails::getPostTown, NEVER_SHOW)
                .readonly(PropertyDetails::getCounty, NEVER_SHOW)
                .readonly(PropertyDetails::getPostcode, NEVER_SHOW)
            .done()
            .complex(PTCase::getLandlordDetails, NEVER_SHOW)
                .complex(LandlordDetails::getLandlordPartyDetails, NEVER_SHOW)
                    .readonly(PartyDetails::getFirstName)
                    .readonly(PartyDetails::getLastName)
                .done()
            .done()
            .pageLabel("Request vetting information")
            .label(deriveSummaryKey(), CCD_CASE_SUMMARY_HTML)
            .label(deriveLineSeparatorKey(), LINE_SEPARATOR)
            .mandatory(PTCase::getVettingInfoRequested, null, null,
                       MAIN_QUESTION, HINT_SELECT_ALL_THAT_APPLY)
            .mandatory(PTCase::getVettingInfoOther, OTHER_CONDITION, null,
                       OTHER_QUESTION, OTHER_QUESTION_HINT);
    }

    @Override
    protected String getPageKey() {
        return CcdPage.derivePageKey(this.getClass());
    }
}
