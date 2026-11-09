package uk.gov.hmcts.reform.pt.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.service.CaseApplicationService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RespondentApplicationControllerTest {

    private static final String AUTH_TOKEN = "Bearer user-token";
    private static final String S2S_TOKEN = "s2s-token";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private IdamAuthenticator idamAuthenticator;

    @Mock
    private CaseApplicationService caseApplicationService;

    @InjectMocks
    private RespondentApplicationController underTest;

    private final UUID userId = UUID.randomUUID();

    private void givenAuthenticatedUser(String uid) {
        UserInfo user = UserInfo.builder().uid(uid).build();
        when(idamAuthenticator.validateAuthToken(AUTH_TOKEN).getUserDetails()).thenReturn(user);
    }

    @Test
    void shouldReturnRespondentCasesForAuthenticatedUser() {
        givenAuthenticatedUser(userId.toString());
        List<ApplicationDto> applications = List.of(ApplicationDto.builder().build());
        when(caseApplicationService.getRespondentCasesForUser(userId)).thenReturn(applications);

        ResponseEntity<List<ApplicationDto>> response =
            underTest.getCasesForRespondentUser(AUTH_TOKEN, S2S_TOKEN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(applications);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoRespondentCases() {
        givenAuthenticatedUser(userId.toString());
        when(caseApplicationService.getRespondentCasesForUser(userId)).thenReturn(List.of());

        ResponseEntity<List<ApplicationDto>> response =
            underTest.getCasesForRespondentUser(AUTH_TOKEN, S2S_TOKEN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldLookUpCasesUsingUidFromAuthToken() {
        givenAuthenticatedUser(userId.toString());

        underTest.getCasesForRespondentUser(AUTH_TOKEN, S2S_TOKEN);

        verify(caseApplicationService).getRespondentCasesForUser(userId);
    }

    @Test
    void shouldNotCallServiceWhenAuthTokenIsInvalid() {
        when(idamAuthenticator.validateAuthToken(any()))
            .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> underTest.getCasesForRespondentUser(AUTH_TOKEN, S2S_TOKEN))
            .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(caseApplicationService);
    }
}
