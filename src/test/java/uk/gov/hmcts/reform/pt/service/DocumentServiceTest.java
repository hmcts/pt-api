package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.UploadedDocument;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;
import uk.gov.hmcts.reform.pt.service.document.CaseDocumentService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory.UNCATEGORISED_DOCUMENTS;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String CDAM_URL = "http://cdam/cases/documents/6f1b1c2e-3a4d-4b5c-8d9e-0f1a2b3c4d5e";

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private CaseDocumentService caseDocumentService;

    @InjectMocks
    private DocumentService documentService;

    @Nested
    class DocumentServiceTestUpdateDocuments {
        @Test
        @DisplayName("Should create new DocumentEntity when single document does not already exist")
        void updateSingleDocumentCreatesNew() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument uploadedDocument = uploadedDocument("notice", DocumentType.NEW_RENT_INCREASE_NOTICE);

            documentService.updateSingleDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

            DocumentEntity saved = captureSingleSave();

            assertThat(saved.getCategoryId()).isEqualTo(UNCATEGORISED_DOCUMENTS.getId());
            assertRequiredDocumentEntityProperties(
                ptCase,
                uploadedDocument,
                saved
            );
        }

        @Test
        @DisplayName("Should update existing DocumentEntity when single document of that type already exists")
        void updateSingleDocumentUpdatesExisting() {
            DocumentEntity existing = existingDocument("old", DocumentType.NEW_RENT_INCREASE_NOTICE);

            PTCaseEntity ptCase = caseWithDocuments(existing);

            UploadedDocument uploadedDocument = uploadedDocument("new_notice", DocumentType.NEW_RENT_INCREASE_NOTICE);

            documentService.updateSingleDocument(DocumentType.NEW_RENT_INCREASE_NOTICE, uploadedDocument, ptCase);

            verify(documentRepository).save(existing);
            assertRequiredDocumentEntityProperties(
                ptCase,
                uploadedDocument,
                existing
            );
            assertThat(existing.getCategoryId()).isEqualTo(UNCATEGORISED_DOCUMENTS.getId());
        }

        @Test
        @DisplayName("Should leave an existing document alone when the incoming field is null")
        void updateSingleDocumentLeavesExistingWhenUploadedDocumentIsNull() {
            DocumentEntity existing = existingDocument("hardship", DocumentType.HARDSHIP_EVIDENCE);
            PTCaseEntity ptCase = caseWithDocuments(existing);

            documentService.updateSingleDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

            verify(documentRepository, never()).delete(existing);
            verify(documentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update nothing when single uploaded document is null and no existing document exists")
        void updateSingleDocumentDoesNothingWhenUploadedDocumentIsNullAndNoExistingDocument() {
            PTCaseEntity ptCase = caseWithDocuments();

            documentService.updateSingleDocument(DocumentType.HARDSHIP_EVIDENCE, null, ptCase);

            verify(documentRepository, never()).delete(any());
            verify(documentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should upsert multiple documents and leave ones absent from the list alone")
        void updateMultipleDocuments() {
            DocumentEntity existingRoom1 = existingDocument("room1", DocumentType.PROPERTY_ROOMS);
            DocumentEntity existingUntouched = existingDocument("room_untouched", DocumentType.PROPERTY_ROOMS);

            PTCaseEntity ptCase = caseWithDocuments(existingRoom1, existingUntouched);

            UploadedDocument room1Upload = uploadedDocument("room1", DocumentType.PROPERTY_ROOMS);
            UploadedDocument room2Upload = uploadedDocument("room2", DocumentType.PROPERTY_ROOMS);

            documentService.updateMultipleDocuments(
                DocumentType.PROPERTY_ROOMS,
                List.of(new ListValue<>("1", room1Upload), new ListValue<>("2", room2Upload)),
                ptCase
            );

            verify(documentRepository, never()).delete(existingUntouched);
            verify(documentRepository, never()).delete(existingRoom1);

            List<DocumentEntity> saved = captureSaves(2);
            assertThat(saved).hasSize(2);

            DocumentEntity firstSaved = saved.get(0);
            assertThat(firstSaved).isEqualTo(existingRoom1);
            assertRequiredDocumentEntityProperties(ptCase, room1Upload, firstSaved);

            DocumentEntity secondSaved = saved.get(1);
            assertRequiredDocumentEntityProperties(ptCase, room2Upload, secondSaved);
        }

        @Test
        @DisplayName("Should update all documents for notice of rent increase")
        void updateDocumentsForNoticeOfRentChangeUpdatesAllDocuments() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument noticeDoc = uploadedDocument("notice", DocumentType.NEW_RENT_INCREASE_NOTICE);
            UploadedDocument legalDoc = uploadedDocument("invalid", DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE);
            UploadedDocument hardshipDoc = uploadedDocument("hardship", DocumentType.HARDSHIP_EVIDENCE);

            NoticeOfRentIncreaseDetails details = NoticeOfRentIncreaseDetails.builder()
                .landlordNoticeProposingNewRentDocument(noticeDoc)
                .noticeNotLegallyValidDocuments(singleItemList(legalDoc))
                .rentIncreaseHardshipDocuments(singleItemList(hardshipDoc))
                .build();

            documentService.updateDocumentsForNoticeOfRentChange(details, ptCase);

            List<DocumentEntity> savedDocs = captureSaves(3);
            assertThat(savedDocs).extracting(DocumentEntity::getDocumentType)
                .containsExactlyInAnyOrder(
                    DocumentType.NEW_RENT_INCREASE_NOTICE,
                    DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE,
                    DocumentType.HARDSHIP_EVIDENCE);
        }

        @Test
        @DisplayName("Should update all documents for property details")
        void updateDocumentsForPropertyDetailsUpdatesAllDocuments() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument floorPlanDoc = uploadedDocument("floor", DocumentType.PROPERTY_FLOOR_PLAN);
            UploadedDocument outsidePropertyDoc = uploadedDocument("outside", DocumentType.OUTSIDE_PROPERTY);
            UploadedDocument repairsDoc = uploadedDocument("repairs", DocumentType.TENANT_REPAIRS_EVIDENCE);
            UploadedDocument roomDoc = uploadedDocument("room", DocumentType.PROPERTY_ROOMS);

            PropertyDetails details = PropertyDetails.builder()
                .floorPlanDocuments(singleItemList(floorPlanDoc))
                .outsidePropertyDocuments(singleItemList(outsidePropertyDoc))
                .repairsEvidenceDocuments(singleItemList(repairsDoc))
                .roomsDocuments(singleItemList(roomDoc))
                .build();

            documentService.updateDocumentsForPropertyDetails(details, ptCase);

            List<DocumentEntity> savedDocs = captureSaves(4);
            assertThat(savedDocs).extracting(DocumentEntity::getDocumentType)
                .containsExactlyInAnyOrder(
                    DocumentType.PROPERTY_FLOOR_PLAN,
                    DocumentType.OUTSIDE_PROPERTY,
                    DocumentType.TENANT_REPAIRS_EVIDENCE,
                    DocumentType.PROPERTY_ROOMS);
        }

        @Test
        @DisplayName("Should update document for rent details when evidence document is present")
        void updateDocumentsForRentDetailsUpdatesDocument() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument rentDoc = uploadedDocument(
                "market-rent", DocumentType.TENANT_PROPOSED_MARKET_RENT_EVIDENCE);

            RentDetails details = RentDetails.builder()
                .suggestedMarketRentEvidence(singleItemList(rentDoc))
                .build();

            documentService.updateDocumentsForRentDetails(details, ptCase);

            DocumentEntity savedDoc = captureSingleSave();
            assertRequiredDocumentEntityProperties(
                ptCase,
                rentDoc,
                savedDoc
            );
        }

        @Test
        @DisplayName("Should update document for landlord response when tenancy details evidence is present")
        void updateDocumentsForLandlordResponseUpdatesDocument() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument responseDoc = uploadedDocument(
                "tenancy",
                DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS
            );

            LandlordResponse details = LandlordResponse.builder()
                .tenancyDetailsResponseEvidence(singleItemList(responseDoc))
                .build();

            documentService.updateDocumentsForLandlordResponse(details, ptCase);

            DocumentEntity savedDoc = captureSingleSave();
            assertRequiredDocumentEntityProperties(
                ptCase,
                responseDoc,
                savedDoc
            );
        }

        @Test
        @DisplayName("Should update document for landlord response when property details evidence is present")
        void updateDocumentsForLandlordResponseUpdatesPropertyDetailsDocument() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument evidenceDoc =
                uploadedDocument("property", DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_PROPERTY_DETAILS);

            LandlordResponse details = LandlordResponse.builder()
                .propertyDetailsResponseEvidence(singleItemList(evidenceDoc))
                .build();

            documentService.updateDocumentsForLandlordResponse(details, ptCase);

            DocumentEntity savedDoc = captureSingleSave();
            assertRequiredDocumentEntityProperties(
                ptCase,
                evidenceDoc,
                savedDoc
            );
        }

        @Test
        @DisplayName("Should update document for tenancy agreement details when evidence document is present")
        void updateDocumentsForTenancyAgreementDetailsUpdatesDocument() {
            PTCaseEntity ptCase = caseWithDocuments();

            UploadedDocument agreementDoc = uploadedDocument("tenancy-agreement", DocumentType.TENANCY_AGREEMENT);

            TenancyAgreementDetails details = TenancyAgreementDetails.builder()
                .tenancyAgreementDocument(agreementDoc)
                .build();

            documentService.updateDocumentsForTenancyAgreementDetails(details, ptCase);

            DocumentEntity savedDoc = captureSingleSave();

            assertRequiredDocumentEntityProperties(
                ptCase,
                agreementDoc,
                savedDoc
            );
        }
    }

    @Nested
    class DocumentServiceTestDeleteDocuments {
        @Test
        @DisplayName("Should delete the row and the stored file, scoped to the case")
        void deleteDocumentRemovesRowAndStoredFile() {
            DocumentEntity stored = givenStoredDocument(1L, DocumentType.PROPERTY_FLOOR_PLAN);

            assertThat(documentService.deleteDocument(1L, CASE_REFERENCE)).isTrue();

            verify(documentRepository).deleteByIdAndPtCaseCaseReference(1L, CASE_REFERENCE);
            verify(caseDocumentService).deleteDocument(stored.getUrl());
        }

        @Test
        @DisplayName("Should read the URL before deleting the row")
        void deleteDocumentReadsUrlBeforeDeletingRow() {
            givenStoredDocument(1L, DocumentType.PROPERTY_FLOOR_PLAN);

            documentService.deleteDocument(1L, CASE_REFERENCE);

            InOrder inOrder = inOrder(documentRepository, caseDocumentService);
            inOrder.verify(documentRepository).findByIdAndPtCaseCaseReference(1L, CASE_REFERENCE);
            inOrder.verify(documentRepository).deleteByIdAndPtCaseCaseReference(1L, CASE_REFERENCE);
            inOrder.verify(caseDocumentService).deleteDocument(anyString());
        }

        @Test
        @DisplayName("Should delete a document of any type without knowing which type it is")
        void deleteDocumentIsTypeAgnostic() {
            givenStoredDocument(7L, DocumentType.TENANCY_AGREEMENT);

            assertThat(documentService.deleteDocument(7L, CASE_REFERENCE)).isTrue();
        }

        @Test
        @DisplayName("Should touch nothing when the document is not on this case")
        void deleteDocumentDoesNothingWhenNotOnCase() {
            when(documentRepository.findByIdAndPtCaseCaseReference(999L, CASE_REFERENCE))
                .thenReturn(Optional.empty());

            assertThat(documentService.deleteDocument(999L, CASE_REFERENCE)).isFalse();

            verify(documentRepository, never()).deleteByIdAndPtCaseCaseReference(anyLong(), anyLong());
            verify(caseDocumentService, never()).deleteDocument(anyString());
        }

        @Test
        @DisplayName("Should delete a document whose type is one of the allowed types")
        void deleteDocumentOfAllowedTypeRemovesRowAndStoredFile() {
            DocumentEntity stored = givenStoredDocument(3L,
                DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS);

            Set<DocumentType> allowed = Set.of(DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS);

            assertThat(documentService.deleteDocument(3L, CASE_REFERENCE, allowed)).isTrue();

            verify(documentRepository).deleteByIdAndPtCaseCaseReference(3L, CASE_REFERENCE);
            verify(caseDocumentService).deleteDocument(stored.getUrl());
        }

        @Test
        @DisplayName("Should not delete when the document type is not one of the allowed types")
        void deleteDocumentOfOtherTypeDoesNothing() {
            givenStoredDocument(4L, DocumentType.TENANCY_AGREEMENT);

            Set<DocumentType> allowed = Set.of(DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS);

            assertThat(documentService.deleteDocument(4L, CASE_REFERENCE, allowed)).isFalse();

            verify(documentRepository, never()).deleteByIdAndPtCaseCaseReference(anyLong(), anyLong());
            verify(caseDocumentService, never()).deleteDocument(anyString());
        }
    }

    private static void assertRequiredDocumentEntityProperties(PTCaseEntity existingPtCase,
                                                               UploadedDocument uploaded,
                                                               DocumentEntity theDocument) {
        assertThat(existingPtCase).isEqualTo(theDocument.getPtCase());
        assertThat(theDocument.getDocumentType()).isEqualTo(uploaded.getDocumentType());
        assertThat(theDocument.getDescription()).isEqualTo(uploaded.getDocumentType().getLabel());
        assertThat(theDocument.getUrl()).isEqualTo(uploaded.getDocument().getUrl());
        assertThat(theDocument.getFileName()).isEqualTo(uploaded.getDocument().getFilename());
        assertThat(theDocument.getBinaryUrl()).isEqualTo(uploaded.getDocument().getBinaryUrl());
        assertThat(theDocument.getSize()).isEqualTo(uploaded.getSizeInBytes());
        assertThat(theDocument.getContentType()).isEqualTo(uploaded.getContentType());
    }

    private static UploadedDocument uploadedDocument(String rootName, DocumentType documentType) {
        String url = "http://dm-store/documents/" + rootName;
        String binaryUrl = "http://dm-store/binary/binary-" + rootName;
        String filename = rootName + ".pdf";

        return UploadedDocument.builder()
            .document(Document.builder().url(url).filename(filename).binaryUrl(binaryUrl).build())
            .contentType("application/pdf")
            .documentType(documentType)
            .sizeInBytes(1024L)
            .build();
    }

    private static DocumentEntity existingDocument(String rootName, DocumentType documentType) {
        String url = "http://dm-store/documents/" + rootName;
        String binaryUrl = "http://dm-store/binary/binary-" + rootName;
        String filename = rootName + ".pdf";

        return DocumentEntity.builder()
            .documentType(documentType)
            .url(url)
            .fileName(filename)
            .build();
    }

    private static PTCaseEntity caseWithDocuments(DocumentEntity... documents) {
        return PTCaseEntity.builder()
            .documents(List.of(documents))
            .build();
    }

    private static List<ListValue<UploadedDocument>> singleItemList(UploadedDocument document) {
        return List.of(new ListValue<>("1", document));
    }

    private DocumentEntity givenStoredDocument(long id, DocumentType type) {
        DocumentEntity stored = DocumentEntity.builder()
            .documentType(type)
            .url(CDAM_URL)
            .build();
        when(documentRepository.findByIdAndPtCaseCaseReference(id, CASE_REFERENCE))
            .thenReturn(Optional.of(stored));
        return stored;
    }

    private DocumentEntity captureSingleSave() {
        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository).save(captor.capture());
        return captor.getValue();
    }

    private List<DocumentEntity> captureSaves(int expectedSaves) {
        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository, times(expectedSaves)).save(captor.capture());
        return captor.getAllValues();
    }
}
