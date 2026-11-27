package uk.gov.hmcts.reform.pt.service.document;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.docassembly.DocAssemblyClient;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyRequest;
import uk.gov.hmcts.reform.docassembly.domain.DocAssemblyResponse;
import uk.gov.hmcts.reform.docassembly.domain.FormPayload;
import uk.gov.hmcts.reform.docassembly.domain.OutputType;
import uk.gov.hmcts.reform.docassembly.exception.DocumentGenerationFailedException;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;
import uk.gov.hmcts.reform.pt.exception.DocAssemblyException;

@Slf4j
@Service
public class DocAssemblyService {
    private final DocAssemblyClient docAssemblyClient;
    private final IdamTokenProvider systemUpdateUserTokenProvider;
    private final AuthTokenGenerator authTokenGenerator;

    public DocAssemblyService(
        DocAssemblyClient docAssemblyClient,
        @Qualifier("systemUpdateUserTokenProvider") IdamTokenProvider systemUpdateUserTokenProvider,
        AuthTokenGenerator authTokenGenerator
    ) {
        this.docAssemblyClient = docAssemblyClient;
        this.systemUpdateUserTokenProvider = systemUpdateUserTokenProvider;
        this.authTokenGenerator = authTokenGenerator;
    }

    public String generateDocument(FormPayload formPayload, String templateId,
                                   OutputType outputType, String outputFilename) {
        if (formPayload == null) {
            throw new IllegalArgumentException("formPayload cannot be null");
        }

        DocAssemblyRequest request = DocAssemblyRequest.builder()
            .templateId(templateId)
            .outputType(outputType)
            .formPayload(formPayload)
            .outputFilename(outputFilename)
            .caseTypeId(CaseType.getCaseType())
            .jurisdictionId(CaseType.getJurisdictionId())
            .secureDocStoreEnabled(true)
            .build();

        DocAssemblyResponse response;
        try {
            response = docAssemblyClient.generateOrder(
                systemUpdateUserTokenProvider.getAuthToken(), authTokenGenerator.generate(), request);
        } catch (DocumentGenerationFailedException e) {
            throw new DocAssemblyException("Document generation failed for template " + templateId, e);
        }

        String documentUrl = response.getRenditionOutputLocation();
        if (documentUrl == null || documentUrl.isEmpty()) {
            throw new DocAssemblyException("No document URL returned from Doc Assembly for template " + templateId);
        }
        log.info("Generated document from template {}: {}", templateId, documentUrl);
        return documentUrl;
    }
}
