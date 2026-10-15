package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticationFilter;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.model.NotificationResponse;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = TestingSupportController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = IdamAuthenticationFilter.class))
@TestPropertySource(properties = "testing-support.enabled=true")
class TestingSupportControllerTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String AUTH = "Bearer token";
    private static final String S2S = "Bearer s2s";
    private static final String SYSTEM_USER_ROLE = "pt-system-update";
    private static final String CITIZEN_ROLE = "citizen";

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {

        @Bean
        @SuppressWarnings("PMD.SignatureDeclareThrowsException")
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PTCaseService ptCaseService;

    @MockitoBean
    private PTCaseRepository ptCaseRepository;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @Test
    @WithMockUser(authorities = SYSTEM_USER_ROLE)
    void shouldDeleteCaseAndReturnNoContentForSystemUser() throws Exception {
        doNothing().when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE).with(csrf()))
            .andExpect(status().isNoContent());

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    @WithMockUser(authorities = SYSTEM_USER_ROLE)
    void shouldReturnNotFoundWhenCaseDoesNotExist() throws Exception {
        doThrow(new CaseNotFoundException(CASE_REFERENCE)).when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE).with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No case found with reference " + CASE_REFERENCE));

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    @WithMockUser(authorities = CITIZEN_ROLE)
    void shouldReturnForbiddenForNonSystemUser() throws Exception {
        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE).with(csrf()))
            .andExpect(status().isForbidden());

        verify(ptCaseService, never()).deleteCase(anyLong());
    }

    @Test
    void shouldReturnForbiddenWhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE).with(csrf()))
            .andExpect(status().isForbidden());

        verify(ptCaseService, never()).deleteCase(anyLong());
    }

    @Test
    void shouldSendTestNotificationWhenCaseDoesNotExist() throws Exception {
        PTCaseEntity savedPtCase = PTCaseEntity.builder().id(100L).caseReference(1234123412341234L).build();
        when(ptCaseRepository.findByCaseReference(1234123412341234L)).thenReturn(Optional.empty());
        when(ptCaseRepository.save(any(PTCaseEntity.class))).thenReturn(savedPtCase);

        NotificationResponse response = NotificationResponse.builder()
            .taskId("task-123")
            .status(NotificationStatus.SCHEDULED.toString())
            .notificationId(50L)
            .build();
        when(notificationService.scheduleEmailNotification(any(NotificationRequest.class))).thenReturn(response);

        String requestJson = """
            {
                "emailAddress": "test@example.com",
                "testReference": "REF-123"
            }
            """;

        mockMvc.perform(post("/testing-support/notify-test")
                .header("Authorization", AUTH)
                .header("ServiceAuthorization", S2S)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value("task-123"))
            .andExpect(jsonPath("$.status").value(NotificationStatus.SCHEDULED.toString()))
            .andExpect(jsonPath("$.notificationId").value(50L));

        verify(ptCaseRepository).findByCaseReference(1234123412341234L);

        ArgumentCaptor<PTCaseEntity> caseCaptor = ArgumentCaptor.forClass(PTCaseEntity.class);
        verify(ptCaseRepository).save(caseCaptor.capture());
        assertThat(caseCaptor.getValue().getCaseReference()).isEqualTo(1234123412341234L);

        ArgumentCaptor<NotificationRequest> requestCaptor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationService).scheduleEmailNotification(requestCaptor.capture());
        NotificationRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getTemplate()).isEqualTo(EmailTemplate.TEST_TEMPLATE);
        assertThat(capturedRequest.getEmailAddress()).isEqualTo("test@example.com");
        assertThat(capturedRequest.getPersonalisation()).isEqualTo(Map.of("testReference", "REF-123"));
        assertThat(capturedRequest.getPtCase()).isEqualTo(savedPtCase);
    }

    @Test
    void shouldSendTestNotificationWhenCaseAlreadyExists() throws Exception {
        PTCaseEntity existingPtCase = PTCaseEntity.builder().id(200L).caseReference(1234123412341234L).build();
        when(ptCaseRepository.findByCaseReference(1234123412341234L)).thenReturn(Optional.of(existingPtCase));

        NotificationResponse response = NotificationResponse.builder()
            .taskId("task-456")
            .status(NotificationStatus.SCHEDULED.toString())
            .notificationId(75L)
            .build();
        when(notificationService.scheduleEmailNotification(any(NotificationRequest.class))).thenReturn(response);

        String requestJson = """
            {
                "emailAddress": "existing@example.com",
                "testReference": "REF-456"
            }
            """;

        mockMvc.perform(post("/testing-support/notify-test")
                .header("Authorization", AUTH)
                .header("ServiceAuthorization", S2S)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value("task-456"))
            .andExpect(jsonPath("$.status").value(NotificationStatus.SCHEDULED.toString()))
            .andExpect(jsonPath("$.notificationId").value(75L));

        verify(ptCaseRepository).findByCaseReference(1234123412341234L);
        verify(ptCaseRepository, never()).save(any(PTCaseEntity.class));

        ArgumentCaptor<NotificationRequest> requestCaptor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationService).scheduleEmailNotification(requestCaptor.capture());
        NotificationRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getTemplate()).isEqualTo(EmailTemplate.TEST_TEMPLATE);
        assertThat(capturedRequest.getEmailAddress()).isEqualTo("existing@example.com");
        assertThat(capturedRequest.getPersonalisation()).isEqualTo(Map.of("testReference", "REF-456"));
        assertThat(capturedRequest.getPtCase()).isEqualTo(existingPtCase);
    }

    @Test
    void shouldReturnBadRequestWhenAuthorizationHeaderIsMissing() throws Exception {
        String requestJson = """
            {
                "emailAddress": "test@example.com",
                "testReference": "REF-123"
            }
            """;

        mockMvc.perform(post("/testing-support/notify-test")
                .header("ServiceAuthorization", S2S)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenServiceAuthorizationHeaderIsMissing() throws Exception {
        String requestJson = """
            {
                "emailAddress": "test@example.com",
                "testReference": "REF-123"
            }
            """;

        mockMvc.perform(post("/testing-support/notify-test")
                .header("Authorization", AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}
