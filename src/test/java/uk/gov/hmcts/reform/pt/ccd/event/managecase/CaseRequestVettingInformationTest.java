package uk.gov.hmcts.reform.pt.ccd.event.managecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;
import uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo.CaseRequestVettingInformationConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaseRequestVettingInformationTest extends BaseEventTest {

    @Mock
    private CaseRequestVettingInformationConfigurer pageConfigurer;

    private CaseRequestVettingInformation underTest;

    @BeforeEach
    void setUp() {
        underTest = new CaseRequestVettingInformation(pageConfigurer);
        configureEvent(underTest);
    }

    @Test
    void shouldConfigurePages() {
        verify(pageConfigurer).configurePages(any(PageBuilder.class));
    }

    @Test
    void shouldReturnCaseProgressionStateAndConfirmationBodyOnSubmit() {
        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("10 High Street")
            .postTown("London")
            .postcode("SW1A 1AA")
            .build();
        PartyDetails landlordPartyDetails = PartyDetails.builder()
            .firstName("John")
            .lastName("Smith")
            .build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordPartyDetails)
            .build();

        PTCase caseData = PTCase.builder()
            .applicantFirstName("Jane")
            .applicantLastName("Doe")
            .propertyDetails(propertyDetails)
            .landlordDetails(landlordDetails)
            .build();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        assertThat(result.getState()).isEqualTo(State.CASE_PROGRESSION);
        assertThat(result.getConfirmationBody())
            .contains("Vetting information requested")
            .contains("Case number: " + TEST_CASE_REFERENCE)
            .contains("10 High Street, London, SW1A 1AA")
            .contains("Jane Doe vs John Smith")
            .contains("/cases/case-details/" + TEST_CASE_REFERENCE);
    }
}
