package uk.gov.hmcts.reform.pt.ccd.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenCreateApplicationTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @Mock
    private SecurityContextService securityContextService;

    @BeforeEach
    void setUp() {
        CitizenCreateApplication underTest = new CitizenCreateApplication(ptCaseService, securityContextService);
        configureEvent(underTest);
    }

    @Test
    void startShouldReturnCaseDataUnchanged() {
        PTCase caseData = getTestPTCase();

        PTCase result = callStartHandler(caseData);

        assertThat(result).isSameAs(caseData);
        verifyNoInteractions(ptCaseService);
    }

    @Test
    void submitShouldCreateCaseForAuthenticatedUserAndReturnDefaultResponse() {
        UUID userId = UUID.randomUUID();
        when(securityContextService.getCurrentUserDetails())
            .thenReturn(UserInfo.builder().uid(userId.toString()).build());
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).createCase(TEST_CASE_REFERENCE, userId, caseData);
        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }

    private PTCase getTestPTCase() {
        return PTCase.builder()
            .applicantFirstName("Jane")
            .applicantLastName("Doe")
            .email("jane.doe@example.com")
            .postcode("AB1 2CD")
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .build();
    }
}
