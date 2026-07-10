package uk.gov.hmcts.reform.pt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.dto.CreateApplicationRequestDto;
import uk.gov.hmcts.reform.pt.dto.CreateApplicationResponseDto;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void shouldCreateApplication() throws Exception {
        UUID userId = UUID.randomUUID();

        UserInfo userInfo = UserInfo.builder()
            .uid(userId.toString())
            .build();
        User user = new User(AUTH, userInfo);

        CreateApplicationRequestDto request = CreateApplicationRequestDto.builder()
            .applicantFirstName("John")
            .applicantLastName("Smith")
            .email("john.smith@example.com")
            .postcode("SW1A 1AA")
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .build();

        CreateApplicationResponseDto response = CreateApplicationResponseDto.builder()
            .caseReference(CASE_REFERENCE)
            .build();

        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(user);
        when(ptCaseService.createCase(any(CreateApplicationRequestDto.class), eq(userId)))
            .thenReturn(response);

        mockMvc.perform(post("/applications")
                            .header("Authorization", AUTH)
                            .header("ServiceAuthorization", S2S)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.caseReference").value(CASE_REFERENCE));

        verify(idamAuthenticator).validateAuthToken(AUTH);
        verify(ptCaseService).createCase(any(CreateApplicationRequestDto.class), eq(userId));
    }

    @Test
    void shouldGetApplicationsForUser() throws Exception {
        UUID userId = UUID.randomUUID();

        UserInfo userInfo = UserInfo.builder()
            .uid(userId.toString())
            .build();
        User user = new User(AUTH, userInfo);

        CaseDto application = CaseDto.builder()
            .caseReference(CASE_REFERENCE)
            .build();

        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(user);
        when(ptCaseService.getCasesForUser(userId))
            .thenReturn(List.of(application));

        mockMvc.perform(get("/applications")
                            .header("Authorization", AUTH)
                            .header("ServiceAuthorization", S2S)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].caseReference").value(CASE_REFERENCE));

        verify(idamAuthenticator).validateAuthToken(AUTH);
        verify(ptCaseService).getCasesForUser(userId);
    }

    @Test
    void shouldGetApplicationByCaseReference() throws Exception {
        CaseDto application = CaseDto.builder()
            .caseReference(CASE_REFERENCE)
            .build();

        UserInfo userInfo = UserInfo.builder()
            .uid(UUID.randomUUID().toString())
            .build();
        User user = new User(AUTH, userInfo);

        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(user);
        when(ptCaseService.getCaseByCaseReference(eq(CASE_REFERENCE), any(UUID.class)))
            .thenReturn(application);

        mockMvc.perform(get("/applications/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH)
                            .header("ServiceAuthorization", S2S)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.caseReference").value(CASE_REFERENCE));

        verify(idamAuthenticator).validateAuthToken(AUTH);
        verify(ptCaseService).getCaseByCaseReference(eq(CASE_REFERENCE), any(UUID.class));
    }
}
