package uk.gov.hmcts.reform.pt.ccd.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.config.AbstractPostgresContainerIT;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@DisplayName("CreateTestCase Integration Tests")
public class CreateTestCaseIT extends AbstractPostgresContainerIT {

    private CreateTestCase underTest;

    private CaseDetails<PTCase, State> caseDetails;

    @BeforeEach
    void setUp() {
        underTest = new CreateTestCase();

        PTCase ptCase = PTCase.builder().build();
        caseDetails = CaseDetails.<PTCase, State>builder().data(ptCase).state(State.AWAITING_SUBMISSION_TO_HMCTS).build();
    }

    @Test
    @DisplayName("Should return case data on aboutToSubmit")
    void shouldReturnCaseDataOnAboutToSubmit() {
        AboutToStartOrSubmitResponse<PTCase, State> response =
            underTest.aboutToSubmit(caseDetails, caseDetails);

        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotNull();
    }

    @Test
    @DisplayName("Should return data from current details, not before details on aboutToSubmit")
    void shouldReturnCurrentDetailsDataOnAboutToSubmit() {
        PTCase currentData = PTCase.builder().build();
        PTCase beforeData = PTCase.builder().build();

        CaseDetails<PTCase, State> before = CaseDetails.<PTCase, State>builder().data(beforeData).build();
        caseDetails.setData(currentData);

        AboutToStartOrSubmitResponse<PTCase, State> response =
            underTest.aboutToSubmit(caseDetails, before);

        assertThat(response.getData()).isSameAs(currentData);
        assertThat(response.getData()).isNotSameAs(beforeData);
    }

    @Test
    @DisplayName("Should return no errors on aboutToSubmit")
    void shouldReturnNoErrorsOnAboutToSubmit() {
        AboutToStartOrSubmitResponse<PTCase, State> response =
            underTest.aboutToSubmit(caseDetails, caseDetails);

        assertThat(response.getErrors()).isNullOrEmpty();
    }

    @Test
    @DisplayName("Should return no warnings on aboutToSubmit")
    void shouldReturnNoWarningsOnAboutToSubmit() {
        AboutToStartOrSubmitResponse<PTCase, State> response =
            underTest.aboutToSubmit(caseDetails, caseDetails);

        assertThat(response.getWarnings()).isNullOrEmpty();
    }
}
