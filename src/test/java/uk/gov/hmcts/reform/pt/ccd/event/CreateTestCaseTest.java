package uk.gov.hmcts.reform.pt.ccd.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTestCaseTest {

    private CreatePTCase underTest;

    @Mock
    private CaseDetails<PTCase, State> caseDetails;

    @Mock
    private CaseDetails<PTCase, State> beforeDetails;

    @BeforeEach
    void setUp() {
        underTest = new CreatePTCase();
    }

    @Test
    void aboutToSubmitReturnsCaseDataFromDetails() {
        PTCase data = PTCase.builder().build();
        when(caseDetails.getData()).thenReturn(data);

        AboutToStartOrSubmitResponse<PTCase, State> response = underTest.aboutToSubmit(caseDetails, beforeDetails);

        assertThat(response.getData()).isSameAs(data);
    }

    @Test
    void aboutToSubmitDoesNotReturnDataFromBeforeDetails() {
        PTCase currentData = PTCase.builder().build();
        PTCase beforeData = PTCase.builder().build();
        when(caseDetails.getData()).thenReturn(currentData);

        AboutToStartOrSubmitResponse<PTCase, State> response = underTest.aboutToSubmit(caseDetails, beforeDetails);

        assertThat(response.getData()).isNotSameAs(beforeData);
    }

    @Test
    void aboutToSubmitReturnsNoErrors() {
        when(caseDetails.getData()).thenReturn(PTCase.builder().build());

        AboutToStartOrSubmitResponse<PTCase, State> response = underTest.aboutToSubmit(caseDetails, beforeDetails);

        assertThat(response.getErrors()).isNullOrEmpty();
    }

    @Test
    void aboutToSubmitReturnsNoWarnings() {
        when(caseDetails.getData()).thenReturn(PTCase.builder().build());

        AboutToStartOrSubmitResponse<PTCase, State> response = underTest.aboutToSubmit(caseDetails, beforeDetails);

        assertThat(response.getWarnings()).isNullOrEmpty();
    }
}
