package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.UploadedDocument;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory.UNCATEGORISED_DOCUMENTS;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    @DisplayName("Should create new DocumentEntity when document does not already exist")
    void updateDocumentCreatesNew() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(Collections.emptyList())
            .build();

        Document doc = Document.builder()
            .url("http://dm-store/documents/123")
            .filename("notice.pdf")
            .binaryUrl("http://dm-store/documents/123/binary")
            .build();

        UploadedDocument uploadedDocument = UploadedDocument.builder()
            .document(doc)
            .contentType("application/pdf")
            .sizeInBytes(1024L)
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .build();

        documentService.updateDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository).save(captor.capture());
        DocumentEntity saved = captor.getValue();

        assertThat(saved.getPtCase()).isEqualTo(ptCase);
        assertThat(saved.getUrl()).isEqualTo("http://dm-store/documents/123");
        assertThat(saved.getFileName()).isEqualTo("notice.pdf");
        assertThat(saved.getBinaryUrl()).isEqualTo("http://dm-store/documents/123/binary");
        assertThat(saved.getDescription()).isEqualTo(DocumentType.NEW_RENT_INCREASE_NOTICE.getLabel());
        assertThat(saved.getDocumentType()).isEqualTo(DocumentType.NEW_RENT_INCREASE_NOTICE);
        assertThat(saved.getSize()).isEqualTo(1024L);
        assertThat(saved.getContentType()).isEqualTo("application/pdf");
        assertThat(saved.getCategoryId()).isEqualTo(UNCATEGORISED_DOCUMENTS.getId());
    }

    @Test
    @DisplayName("Should update existing DocumentEntity when document of that type already exists")
    void updateDocumentUpdatesExisting() {
        DocumentEntity existing = DocumentEntity.builder()
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .url("http://dm-store/documents/old")
            .fileName("old_notice.pdf")
            .binaryUrl("http://dm-store/documents/old/binary")
            .build();

        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(List.of(existing))
            .build();

        Document doc = Document.builder()
            .url("http://dm-store/documents/new")
            .filename("new_notice.pdf")
            .binaryUrl("http://dm-store/documents/new/binary")
            .build();

        UploadedDocument uploadedDocument = UploadedDocument.builder()
            .document(doc)
            .contentType("application/pdf")
            .sizeInBytes(2048L)
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .build();

        documentService.updateDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

        verify(documentRepository).save(existing);
        assertThat(existing.getPtCase()).isEqualTo(ptCase);
        assertThat(existing.getUrl()).isEqualTo("http://dm-store/documents/new");
        assertThat(existing.getFileName()).isEqualTo("new_notice.pdf");
        assertThat(existing.getBinaryUrl()).isEqualTo("http://dm-store/documents/new/binary");
        assertThat(existing.getDescription()).isEqualTo(DocumentType.NEW_RENT_INCREASE_NOTICE.getLabel());
        assertThat(existing.getDocumentType()).isEqualTo(DocumentType.NEW_RENT_INCREASE_NOTICE);
        assertThat(existing.getSize()).isEqualTo(2048L);
        assertThat(existing.getContentType()).isEqualTo("application/pdf");
        assertThat(existing.getCategoryId()).isEqualTo(UNCATEGORISED_DOCUMENTS.getId());
    }

    @Test
    @DisplayName("Should delete existing DocumentEntity when uploaded document is null")
    void updateDocumentDeletesExistingWhenUploadedDocumentIsNull() {
        DocumentEntity existing = DocumentEntity.builder()
            .documentType(DocumentType.HARDSHIP_EVIDENCE)
            .url("http://dm-store/documents/hardship")
            .fileName("hardship.pdf")
            .build();

        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(List.of(existing))
            .build();

        documentService.updateDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

        verify(documentRepository).delete(existing);
        verify(documentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should do nothing when uploaded document is null and no existing document exists")
    void updateDocumentDoesNothingWhenUploadedDocumentIsNullAndNoExistingDocument() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(Collections.emptyList())
            .build();

        documentService.updateDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

        verify(documentRepository, never()).delete(any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update all documents for notice of rent increase")
    void updateDocumentsForNoticeOfRentChangeUpdatesAllDocuments() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(Collections.emptyList())
            .build();

        UploadedDocument noticeDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/1").filename("notice.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(100L)
            .build();
        UploadedDocument invalidNoticeDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/2").filename("invalid.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(200L)
            .build();
        UploadedDocument hardshipDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/3").filename("hardship.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(300L)
            .build();

        NoticeOfRentIncreaseDetails details = NoticeOfRentIncreaseDetails.builder()
            .landlordNoticeProposingNewRentDocument(noticeDoc)
            .noticeNotLegallyValidDocument(invalidNoticeDoc)
            .rentIncreaseToCauseHardshipDocument(hardshipDoc)
            .build();

        documentService.updateDocumentsForNoticeOfRentChange(details, ptCase);

        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, times(3)).save(captor.capture());

        List<DocumentEntity> savedDocs = captor.getAllValues();
        assertThat(savedDocs).extracting(DocumentEntity::getDocumentType)
            .containsExactlyInAnyOrder(
                DocumentType.NEW_RENT_INCREASE_NOTICE,
                DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE,
                DocumentType.HARDSHIP_EVIDENCE);
    }
}
