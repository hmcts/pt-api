package uk.gov.hmcts.reform.pt.service.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.docassembly.DocAssemblyClient;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyRequest;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyResponse;
import uk.gov.hmcts.reform.docassembly.domain.FormPayload;
import uk.gov.hmcts.reform.docassembly.domain.OutputType;
import uk.gov.hmcts.reform.docassembly.exception.DocumentGenerationFailedException;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.exception.DocAssemblyException;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocAssemblyServiceTest {

    private static final String SYSTEM_USER_TOKEN = "Bearer system-user-token";
    private static final String SERVICE_AUTH_TOKEN = "service-auth-token";
    private static final String TEMPLATE_ID = "PT-TEST-TEMPLATE.docx";
    private static final String OUTPUT_FILENAME = "generated-document.pdf";
    private static final String DOCUMENT_URL =
        "http://ccd-case-document-am-api/cases/documents/6f1b1c2e-3a4d-4b5c-8d9e-0f1a2b3c4d5e";

    @Mock
    private DocAssemblyClient docAssemblyClient;

    @Mock
    private IdamTokenProvider systemUpdateUserTokenProvider;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private FormPayload formPayload;

    private DocAssemblyService docAssemblyService;

    @BeforeEach
    void setUp() {
        docAssemblyService = new DocAssemblyService(docAssemblyClient, systemUpdateUserTokenProvider,
                                                    authTokenGenerator);
    }

    @Test
    @DisplayName("Should return the document URL from Doc Assembly")
    void returnsDocumentUrl() {
        stubTokens();
        DocAssemblyResponse response = responseWithUrl(DOCUMENT_URL);
        when(docAssemblyClient.generateOrder(eq(SYSTEM_USER_TOKEN), eq(SERVICE_AUTH_TOKEN),
                                             any(DocAssemblyRequest.class)))
            .thenReturn(response);

        String result = docAssemblyService.generateDocument(formPayload, TEMPLATE_ID, OutputType.PDF,
                                                            OUTPUT_FILENAME);

        assertThat(result).isEqualTo(DOCUMENT_URL);
    }

    @Test
    @DisplayName("Should send the template, payload and PT case details to Doc Assembly")
    void sendsExpectedRequest() {
        stubTokens();
        DocAssemblyResponse response = responseWithUrl(DOCUMENT_URL);
        when(docAssemblyClient.generateOrder(eq(SYSTEM_USER_TOKEN), eq(SERVICE_AUTH_TOKEN),
                                             any(DocAssemblyRequest.class)))
            .thenReturn(response);

        docAssemblyService.generateDocument(formPayload, TEMPLATE_ID, OutputType.PDF, OUTPUT_FILENAME);

        ArgumentCaptor<DocAssemblyRequest> captor = ArgumentCaptor.forClass(DocAssemblyRequest.class);
        verify(docAssemblyClient).generateOrder(eq(SYSTEM_USER_TOKEN), eq(SERVICE_AUTH_TOKEN), captor.capture());
        DocAssemblyRequest request = captor.getValue();
        assertThat(new String(Base64.getDecoder().decode(request.getTemplateId()), StandardCharsets.UTF_8))
            .isEqualTo(TEMPLATE_ID);
        assertThat(request.getOutputType()).isEqualTo(OutputType.PDF);
        assertThat(request.getFormPayload()).isSameAs(formPayload);
        assertThat(request.getOutputFilename()).isEqualTo(OUTPUT_FILENAME);
        assertThat(request.getCaseTypeId()).isEqualTo(CaseType.getCaseType());
        assertThat(request.getJurisdictionId()).isEqualTo(CaseType.getJurisdictionId());
        assertThat(request.isSecureDocStoreEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should reject a null payload without calling Doc Assembly")
    void rejectsNullPayload() {
        assertThatThrownBy(() -> docAssemblyService.generateDocument(null, TEMPLATE_ID, OutputType.PDF,
                                                                     OUTPUT_FILENAME))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("formPayload cannot be null");

        verifyNoInteractions(docAssemblyClient, systemUpdateUserTokenProvider, authTokenGenerator);
    }

    @Test
    @DisplayName("Should wrap a Doc Assembly failure in a DocAssemblyException")
    void wrapsGenerationFailure() {
        stubTokens();
        DocumentGenerationFailedException failure =
            new DocumentGenerationFailedException(new RuntimeException("render failed"));
        when(docAssemblyClient.generateOrder(eq(SYSTEM_USER_TOKEN), eq(SERVICE_AUTH_TOKEN),
                                             any(DocAssemblyRequest.class)))
            .thenThrow(failure);

        assertThatThrownBy(() -> docAssemblyService.generateDocument(formPayload, TEMPLATE_ID, OutputType.PDF,
                                                                     OUTPUT_FILENAME))
            .isInstanceOf(DocAssemblyException.class)
            .hasMessage("Document generation failed for template " + TEMPLATE_ID)
            .hasCause(failure);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw when Doc Assembly returns no document URL")
    void throwsWhenNoDocumentUrl(String documentUrl) {
        stubTokens();
        DocAssemblyResponse response = responseWithUrl(documentUrl);
        when(docAssemblyClient.generateOrder(eq(SYSTEM_USER_TOKEN), eq(SERVICE_AUTH_TOKEN),
                                             any(DocAssemblyRequest.class)))
            .thenReturn(response);

        assertThatThrownBy(() -> docAssemblyService.generateDocument(formPayload, TEMPLATE_ID, OutputType.PDF,
                                                                     OUTPUT_FILENAME))
            .isInstanceOf(DocAssemblyException.class)
            .hasMessage("No document URL returned from Doc Assembly for template " + TEMPLATE_ID);
    }

    private void stubTokens() {
        when(systemUpdateUserTokenProvider.getAuthToken()).thenReturn(SYSTEM_USER_TOKEN);
        when(authTokenGenerator.generate()).thenReturn(SERVICE_AUTH_TOKEN);
    }

    private DocAssemblyResponse responseWithUrl(String documentUrl) {
        DocAssemblyResponse response = mock(DocAssemblyResponse.class);
        when(response.getRenditionOutputLocation()).thenReturn(documentUrl);
        return response;
    }
}
