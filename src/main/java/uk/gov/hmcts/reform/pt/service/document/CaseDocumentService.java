package uk.gov.hmcts.reform.pt.service.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseDocumentService {

    private static final boolean PERMANENT_DELETE = true;

    private final CaseDocumentClientApi caseDocumentClientApi;
    private final AuthTokenGenerator authTokenGenerator;
    private final SecurityContextService securityContextService;

    public void deleteDocument(String documentUrl) {
        UUID documentId = extractDocumentId(documentUrl);
        if (documentId == null) {
            log.error("Could not determine a document id from URL, leaving the stored file in place");
            return;
        }

        try {
            caseDocumentClientApi.deleteDocument(
                securityContextService.getCurrentUserAuthToken(),
                authTokenGenerator.generate(),
                documentId,
                PERMANENT_DELETE
            );
        } catch (Exception e) {
            log.error("Failed to delete document {} from CDAM", documentId, e);
        }
    }

    private UUID extractDocumentId(String documentUrl) {
        if (documentUrl == null) {
            return null;
        }

        int lastSlash = documentUrl.lastIndexOf('/');
        if (lastSlash < 0) {
            return null;
        }

        try {
            return UUID.fromString(documentUrl.substring(lastSlash + 1));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
