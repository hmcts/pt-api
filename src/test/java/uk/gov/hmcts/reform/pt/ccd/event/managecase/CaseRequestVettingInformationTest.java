package uk.gov.hmcts.reform.pt.ccd.event.managecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;
import uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo.CaseRequestVettingInformationConfigurer;
import uk.gov.hmcts.reform.pt.service.CaseVettingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaseRequestVettingInformationTest extends BaseEventTest {

    @Mock
    private CaseRequestVettingInformationConfigurer pageConfigurer;

    @Mock
    private CaseVettingService caseVettingService;

    private CaseRequestVettingInformation underTest;

    @BeforeEach
    void setUp() {
        underTest = new CaseRequestVettingInformation(pageConfigurer, caseVettingService);
        configureEvent(underTest);
    }

    @Test
    void shouldConfigurePages() {
        verify(pageConfigurer).configurePages(any(PageBuilder.class));
    }

    @Test
    void shouldUpdateCaseVettingAndReturnConfirmationBodyOnSubmit() {
        PTCase caseData = PTCase.builder()
            .formattedPropertyAddress("10 High Street, London, SW1A 1AA")
            .formattedCaseName("Jane Doe vs John Smith")
            .build();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(caseVettingService).updateCaseVetting(TEST_CASE_REFERENCE, caseData);
        assertThat(result.getState()).isNull();
        assertThat(result.getConfirmationBody())
            .contains("Vetting information requested")
            .contains("Case number: " + TEST_CASE_REFERENCE)
            .contains("10 High Street, London, SW1A 1AA")
            .contains("Jane Doe vs John Smith")
            .contains("/cases/case-details/" + TEST_CASE_REFERENCE);
    }
}
