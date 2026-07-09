package uk.gov.hmcts.reform.pt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApplicationControllerTest {

    private static final String AUTH = "Bearer token";
    private static final String S2S = "Bearer s2s";
    private static final long CASE_REFERENCE = 1234567890123456L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @MockitoBean
    private PTCaseService ptCaseService;

    @Test
    void shouldGetApplicationsForUser() throws Exception {
        UUID userId = UUID.randomUUID();

        UserInfo userInfo = UserInfo.builder()
            .uid(userId.toString())
            .build();
        User user = new User(AUTH, userInfo);

        CaseDto application = CaseDto.builder()
            .id(UUID.randomUUID())
            .caseReference(CASE_REFERENCE)
            .build();

        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(user);
        when(ptCaseService.getApplicationsForUser(userId))
            .thenReturn(List.of(application));

        mockMvc.perform(get("/applications")
                            .header("Authorization", AUTH)
                            .header("ServiceAuthorization", S2S)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(application.getId().toString()))
            .andExpect(jsonPath("$[0].caseReference").value(CASE_REFERENCE));

        verify(idamAuthenticator).validateAuthToken(AUTH);
        verify(ptCaseService).getApplicationsForUser(userId);
    }

    @Test
    void shouldGetApplicationByCaseReference() throws Exception {
        CaseDto application = CaseDto.builder()
            .id(UUID.randomUUID())
            .caseReference(CASE_REFERENCE)
            .build();

        UserInfo userInfo = UserInfo.builder()
            .uid(UUID.randomUUID().toString())
            .build();
        User user = new User(AUTH, userInfo);

        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(user);
        when(ptCaseService.getApplicationByCaseReference(eq(CASE_REFERENCE), any(UUID.class)))
            .thenReturn(application);

        mockMvc.perform(get("/applications/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH)
                            .header("ServiceAuthorization", S2S)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(application.getId().toString()))
            .andExpect(jsonPath("$.caseReference").value(CASE_REFERENCE));

        verify(idamAuthenticator).validateAuthToken(AUTH);
        verify(ptCaseService).getApplicationByCaseReference(eq(CASE_REFERENCE), any(UUID.class));
    }
}
