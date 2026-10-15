package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.service.document.DocumentValidationErrors.REMOVE_FILE_FIRST;
import static uk.gov.hmcts.reform.pt.service.document.DocumentValidationErrors.TOTAL_TOO_LARGE;

@ExtendWith(MockitoExtension.class)
class DocumentUploadValidatorTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final long MB = 1024L * 1024;

    @Mock
    private DocumentRepository documentRepository;

    private DocumentUploadValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DocumentUploadValidator(documentRepository, 300);
    }

    @Test
    @DisplayName("Should snapshot each document's url by id")
    void snapshotsUrlsById() {
        when(documentRepository.findAllByPtCaseCaseReference(CASE_REFERENCE))
            .thenReturn(List.of(document(1L, "a", DocumentType.PROPERTY_ROOMS, MB)));

        assertThat(validator.snapshot(CASE_REFERENCE)).containsExactly(Map.entry(1L, "a"));
    }

    @Test
    @DisplayName("Should accept a new document that keeps its type within the limit")
    void acceptsDocumentWithinLimit() {
        when(documentRepository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(
            document(1L, "a", DocumentType.TENANT_REPAIRS_EVIDENCE, 200 * MB),
            document(2L, "b", DocumentType.TENANT_REPAIRS_EVIDENCE, 100 * MB)
        ));

        assertThat(validator.errors(CASE_REFERENCE, Map.of(1L, "a"))).isEmpty();
    }

    @Test
    @DisplayName("Should reject a new document that takes its type over the limit")
    void rejectsDocumentOverLimit() {
        when(documentRepository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(
            document(1L, "a", DocumentType.TENANT_REPAIRS_EVIDENCE, 250 * MB),
            document(2L, "b", DocumentType.TENANT_REPAIRS_EVIDENCE, 100 * MB)
        ));

        assertThat(validator.errors(CASE_REFERENCE, Map.of(1L, "a"))).containsExactly(TOTAL_TOO_LARGE.value());
    }

    @Test
    @DisplayName("Should not count a type the update did not touch against the limit")
    void ignoresUntouchedTypeOverLimit() {
        when(documentRepository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(
            document(1L, "a", DocumentType.PROPERTY_ROOMS, 400 * MB),
            document(2L, "b", DocumentType.TENANT_REPAIRS_EVIDENCE, MB)
        ));

        assertThat(validator.errors(CASE_REFERENCE, Map.of(1L, "a"))).isEmpty();
    }

    @Test
    @DisplayName("Should reject an update that replaced an existing document")
    void rejectsReplacedDocument() {
        when(documentRepository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(
            document(1L, "replacement", DocumentType.TENANCY_AGREEMENT, MB)
        ));

        assertThat(validator.errors(CASE_REFERENCE, Map.of(1L, "original"))).containsExactly(REMOVE_FILE_FIRST.value());
    }

    private static DocumentEntity document(long id, String url, DocumentType type, long size) {
        DocumentEntity entity = DocumentEntity.builder().url(url).documentType(type).size(size).build();
        entity.setId(id);
        return entity;
    }
}
