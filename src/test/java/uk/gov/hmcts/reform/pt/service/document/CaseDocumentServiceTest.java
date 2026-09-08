package uk.gov.hmcts.reform.pt.service.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CaseDocumentServiceTest {

    private static final UUID DOCUMENT_ID = UUID.fromString("6f1b1c2e-3a4d-4b5c-8d9e-0f1a2b3c4d5e");
    private static final String DOCUMENT_URL =
        "http://ccd-case-document-am-api-aat.service.core-compute-aat.internal/cases/documents/" + DOCUMENT_ID;

    @Mock
    private CaseDocumentClientApi caseDocumentClientApi;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private CaseDocumentService caseDocumentService;

    @Test
    @DisplayName("Should delete the document, forwarding the citizen's token alongside the S2S token")
    void deletesForwardingBothTokens() {
        when(securityContextService.getCurrentUserAuthToken()).thenReturn("Bearer user-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");

        caseDocumentService.deleteDocument(DOCUMENT_URL);

        verify(caseDocumentClientApi).deleteDocument("Bearer user-token", "s2s-token", DOCUMENT_ID, true);
    }

    @Test
    @DisplayName("Should not fail the event when CDAM is unavailable")
    void swallowsCdamFailure() {
        when(securityContextService.getCurrentUserAuthToken()).thenReturn("Bearer user-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");
        doThrow(new RuntimeException("CDAM is down"))
            .when(caseDocumentClientApi).deleteDocument(anyString(), anyString(), any(), anyBoolean());

        assertThatCode(() -> caseDocumentService.deleteDocument(DOCUMENT_URL)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should not call CDAM when the URL carries no document id")
    void ignoresUnusableUrl() {
        caseDocumentService.deleteDocument("http://cdam/cases/documents/not-a-uuid");
        caseDocumentService.deleteDocument(null);

        verify(caseDocumentClientApi, never()).deleteDocument(anyString(), anyString(), any(), anyBoolean());
    }
}
