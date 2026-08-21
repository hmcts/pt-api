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
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
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
    @DisplayName("Should create new DocumentEntity when single document does not already exist")
    void updateSingleDocumentCreatesNew() {
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

        documentService.updateSingleDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

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
    @DisplayName("Should update existing DocumentEntity when single document of that type already exists")
    void updateSingleDocumentUpdatesExisting() {
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

        documentService.updateSingleDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

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
    @DisplayName("Should delete existing document when single uploaded document is null")
    void updateSingleDocumentDeletesExistingWhenUploadedDocumentIsNull() {
        DocumentEntity existing = DocumentEntity.builder()
            .documentType(DocumentType.HARDSHIP_EVIDENCE)
            .url("http://dm-store/documents/hardship")
            .fileName("hardship.pdf")
            .build();

        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(List.of(existing))
            .build();

        documentService.updateSingleDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

        verify(documentRepository).delete(existing);
        verify(documentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should do nothing when single uploaded document is null and no existing document exists")
    void updateSingleDocumentDoesNothingWhenUploadedDocumentIsNullAndNoExistingDocument() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(Collections.emptyList())
            .build();

        documentService.updateSingleDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

        verify(documentRepository, never()).delete(any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update multiple documents creating new, updating existing, and deleting removed documents")
    void updateMultipleDocuments() {
        DocumentEntity existingDoc = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_ROOMS)
            .url("http://dm-store/documents/room1")
            .fileName("room1.pdf")
            .build();

        DocumentEntity removedDoc = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_ROOMS)
            .url("http://dm-store/documents/room-removed")
            .fileName("room_removed.pdf")
            .build();

        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(List.of(existingDoc, removedDoc))
            .build();

        UploadedDocument updatedDoc = UploadedDocument.builder()
            .document(Document.builder()
                .url("http://dm-store/documents/room1")
                .filename("room1_updated.pdf")
                .binaryUrl("http://dm-store/documents/room1/binary")
                .build())
            .contentType("application/pdf")
            .sizeInBytes(1500L)
            .build();

        UploadedDocument newDoc = UploadedDocument.builder()
            .document(Document.builder()
                .url("http://dm-store/documents/room2")
                .filename("room2.pdf")
                .binaryUrl("http://dm-store/documents/room2/binary")
                .build())
            .contentType("image/jpeg")
            .sizeInBytes(2500L)
            .build();

        documentService.updateMultipleDocuments(DocumentType.PROPERTY_ROOMS, List.of(updatedDoc, newDoc), ptCase);

        verify(documentRepository).delete(removedDoc);
        verify(documentRepository, never()).delete(existingDoc);

        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, times(2)).save(captor.capture());

        List<DocumentEntity> saved = captor.getAllValues();
        assertThat(saved).hasSize(2);

        DocumentEntity firstSaved = saved.get(0);
        assertThat(firstSaved).isEqualTo(existingDoc);
        assertThat(firstSaved.getFileName()).isEqualTo("room1_updated.pdf");
        assertThat(firstSaved.getDocumentType()).isEqualTo(DocumentType.PROPERTY_ROOMS);

        DocumentEntity secondSaved = saved.get(1);
        assertThat(secondSaved.getUrl()).isEqualTo("http://dm-store/documents/room2");
        assertThat(secondSaved.getFileName()).isEqualTo("room2.pdf");
        assertThat(secondSaved.getDocumentType()).isEqualTo(DocumentType.PROPERTY_ROOMS);
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

    @Test
    @DisplayName("Should update all documents for property details")
    void updateDocumentsForPropertyDetailsUpdatesAllDocuments() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .documents(Collections.emptyList())
            .build();

        UploadedDocument floorPlanDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/floor").filename("floor.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(100L)
            .build();
        UploadedDocument outsidePropertyDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/outside").filename("outside.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(200L)
            .build();
        UploadedDocument repairsDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/repairs").filename("repairs.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(300L)
            .build();
        UploadedDocument roomDoc = UploadedDocument.builder()
            .document(Document.builder().url("http://dm-store/documents/room").filename("room.pdf").build())
            .contentType("application/pdf")
            .sizeInBytes(400L)
            .build();

        PropertyDetails details = PropertyDetails.builder()
            .floorPlanDocument(floorPlanDoc)
            .outsidePropertyDocument(outsidePropertyDoc)
            .repairsEvidenceDocument(repairsDoc)
            .propertyRoomsDocuments(List.of(roomDoc))
            .build();

        documentService.updateDocumentsForPropertyDetails(details, ptCase);

        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, times(4)).save(captor.capture());

        List<DocumentEntity> savedDocs = captor.getAllValues();
        assertThat(savedDocs).extracting(DocumentEntity::getDocumentType)
            .containsExactlyInAnyOrder(
                DocumentType.FLOOR_PLAN,
                DocumentType.OUTSIDE_PROPERTY,
                DocumentType.REPAIRS_EVIDENCE,
                DocumentType.PROPERTY_ROOMS);
    }
}
