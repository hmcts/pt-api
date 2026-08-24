package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestingSupportController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "testing-support.enabled=true")
class TestingSupportControllerTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PTCaseService ptCaseService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    @Test
    void shouldDeleteCaseAndReturnNoContent() throws Exception {
        doNothing().when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isNoContent());

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    void shouldReturnNotFoundWhenCaseDoesNotExist() throws Exception {
        doThrow(new CaseNotFoundException(CASE_REFERENCE)).when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No case found with reference " + CASE_REFERENCE));

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }
}
