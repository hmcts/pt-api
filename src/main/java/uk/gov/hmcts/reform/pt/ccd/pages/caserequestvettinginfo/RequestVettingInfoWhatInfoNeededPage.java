package uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo;

import uk.gov.hmcts.reform.pt.ccd.common.CcdPage;
import uk.gov.hmcts.reform.pt.ccd.common.CcdPageConfiguration;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;

import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.CCD_CASE_SUMMARY_HTML;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.LINE_SEPARATOR;
import static uk.gov.hmcts.reform.pt.ccd.common.CcdConstants.NEVER_SHOW;

public class RequestVettingInfoWhatInfoNeededPage extends CcdPage implements CcdPageConfiguration {
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
            .mandatory(PTCase::getVettingInfoRequested)
            .mandatory(PTCase::getVettingInfoOther, OTHER_CONDITION);
    }

    @Override
    protected String getPageKey() {
        return CcdPage.derivePageKey(this.getClass());
    }
}
