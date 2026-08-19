package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory.UNCATEGORISED_DOCUMENTS;

class DocumentRepositoryTest extends AbstractRepositoryTest<DocumentRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected DocumentRepositoryTest(
        DocumentRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve document entity")
    void saveAndFindDocument() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .hearingRequested(YesOrNo.YES)
            .build();
        ptCaseRepository.save(ptCase);

        DocumentEntity document = DocumentEntity.builder()
            .url("http://dm-store/doc/123")
            .fileName("rent_notice.pdf")
            .binaryUrl("http://dm-store/doc/123/binary")
            .contentType("application/pdf")
            .description(DocumentType.NEW_RENT_INCREASE_NOTICE.getLabel())
            .size(1024L)
            .categoryId(UNCATEGORISED_DOCUMENTS.getId())
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .ptCase(ptCase)
            .build();
        DocumentEntity saved = repository.save(document);

        Optional<DocumentEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUrl()).isEqualTo("http://dm-store/doc/123");
        assertThat(result.get().getFileName()).isEqualTo("rent_notice.pdf");
        assertThat(result.get().getBinaryUrl()).isEqualTo("http://dm-store/doc/123/binary");
        assertThat(result.get().getContentType()).isEqualTo("application/pdf");
        assertThat(result.get().getDescription()).isEqualTo("New rent increase notice");
        assertThat(result.get().getSize()).isEqualTo(1024L);
        assertThat(result.get().getCategoryId()).isEqualTo(UNCATEGORISED_DOCUMENTS.getId());
        assertThat(result.get().getDocumentType()).isEqualTo(DocumentType.NEW_RENT_INCREASE_NOTICE);
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
