package uk.gov.hmcts.reform.pt.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private IdamAuthenticator idamAuthenticator;

    @Mock
    private PTCaseService ptCaseService;

    @InjectMocks
    private ApplicationController applicationController;

    @Test
    @DisplayName("Should authenticate the user, delegate case creation and return 201 with the case reference")
    void createApplication() {
        String authorization = "Bearer token";
        String s2sToken = "s2s-token";
        UUID userId = UUID.randomUUID();

        CreateApplicationRequest request = CreateApplicationRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .postcode("SW1A 1AA")
            .applicationType("myself")
            .build();

        when(idamAuthenticator.validateAuthToken(authorization)).thenReturn(getTestUser(userId));
        CreateApplicationResponse expectedResponse = getTestCreateApplicationResponse();
        when(ptCaseService.createCase(request, userId)).thenReturn(expectedResponse);

        ResponseEntity<CreateApplicationResponse> response =
            applicationController.createApplication(authorization, s2sToken, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(idamAuthenticator).validateAuthToken(authorization);
        verify(ptCaseService).createCase(eq(request), eq(userId));
    }

    private User getTestUser(UUID userId) {
        UserInfo userInfo = UserInfo.builder().uid(userId.toString()).build();
        return new User("foo", userInfo);
    }

    private  CreateApplicationResponse getTestCreateApplicationResponse() {
        return CreateApplicationResponse.builder()
            .caseReference(1234567890123456L)
            .build();
    }
}
