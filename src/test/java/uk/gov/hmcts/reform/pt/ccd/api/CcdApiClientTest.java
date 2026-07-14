package uk.gov.hmcts.reform.pt.ccd.api;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.exception.CcdException;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CITIZEN_CREATE_APPLICATION;

@ExtendWith(MockitoExtension.class)
class CcdApiClientTest {

    @Mock
    private CoreCaseDataApi ccdApi;

    @Mock
    private IdamTokenProvider idamTokenProvider;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Captor
    private ArgumentCaptor<CaseDataContent> caseDataContentCaptor;

    @InjectMocks
    private CcdApiClient ccdApiClient;

    @Test
    @DisplayName("Should start a CCD event and return the start event response")
    void shouldStartEvent() {
        when(idamTokenProvider.getAuthToken()).thenReturn("Bearer idam-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");
        StartEventResponse expectedResponse = StartEventResponse.builder()
            .eventId(CITIZEN_CREATE_APPLICATION.getId())
            .token("event-token")
            .build();
        when(ccdApi.startCase(
            "Bearer idam-token",
            "s2s-token",
            CaseType.getCaseType(),
            CITIZEN_CREATE_APPLICATION.getId()
        )).thenReturn(expectedResponse);

        StartEventResponse response = ccdApiClient.startEvent(CITIZEN_CREATE_APPLICATION);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw a CcdException when starting an event fails")
    void shouldThrowCcdExceptionWhenStartEventFails() {
        when(idamTokenProvider.getAuthToken()).thenReturn("Bearer idam-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");
        FeignException feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("boom");
        when(ccdApi.startCase(any(), any(), any(), any())).thenThrow(feignException);

        assertThatThrownBy(() -> ccdApiClient.startEvent(CITIZEN_CREATE_APPLICATION))
            .isInstanceOf(CcdException.class)
            .hasMessage("Failed to start citizen-create-application event in CCD: boom");
    }

    @Test
    @DisplayName("Should submit case creation with the built case data content and return the case details")
    void shouldSubmitCaseCreation() {
        when(idamTokenProvider.getAuthToken()).thenReturn("Bearer idam-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");
        PTCase ptCase = PTCase.builder().applicantFirstName("Jane").build();
        CaseDetails expectedCaseDetails = CaseDetails.builder().id(1234567890123456L).build();
        when(ccdApi.submitCaseCreation(
            eq("Bearer idam-token"),
            eq("s2s-token"),
            eq(CaseType.getCaseType()),
            any(CaseDataContent.class)
        )).thenReturn(expectedCaseDetails);

        CaseDetails caseDetails =
            ccdApiClient.submitCaseCreation(ptCase, CITIZEN_CREATE_APPLICATION, "event-token");

        assertThat(caseDetails).isEqualTo(expectedCaseDetails);
        verify(ccdApi).submitCaseCreation(
            eq("Bearer idam-token"),
            eq("s2s-token"),
            eq(CaseType.getCaseType()),
            caseDataContentCaptor.capture()
        );
        CaseDataContent submittedContent = caseDataContentCaptor.getValue();
        assertThat(submittedContent.getData()).isEqualTo(ptCase);
        assertThat(submittedContent.getEventToken()).isEqualTo("event-token");
        assertThat(submittedContent.getEvent().getId()).isEqualTo(CITIZEN_CREATE_APPLICATION.getId());
    }

    @Test
    @DisplayName("Should throw a CcdException when submitting case creation fails")
    void shouldThrowCcdExceptionWhenSubmitCaseCreationFails() {
        when(idamTokenProvider.getAuthToken()).thenReturn("Bearer idam-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");
        PTCase ptCase = PTCase.builder().applicantFirstName("Jane").build();
        FeignException feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("boom");
        when(ccdApi.submitCaseCreation(any(), any(), any(), any())).thenThrow(feignException);

        assertThatThrownBy(() ->
            ccdApiClient.submitCaseCreation(ptCase, CITIZEN_CREATE_APPLICATION, "event-token"))
            .isInstanceOf(CcdException.class)
            .hasMessage("Failed to submit case creation for event event-token: boom");
    }
}
