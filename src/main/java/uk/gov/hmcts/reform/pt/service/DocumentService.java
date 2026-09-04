package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.UploadedDocument;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;
import uk.gov.hmcts.reform.pt.service.document.CaseDocumentService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CaseDocumentService caseDocumentService;

    @Transactional
    public void updateDocumentsForNoticeOfRentChange(NoticeOfRentIncreaseDetails details, PTCaseEntity ptCaseEntity) {
        updateSingleDocument(
            DocumentType.NEW_RENT_INCREASE_NOTICE,
            details.getLandlordNoticeProposingNewRentDocument(),
            ptCaseEntity
        );
        updateSingleDocument(
            DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE,
            details.getNoticeNotLegallyValidDocument(),
            ptCaseEntity
        );
        updateSingleDocument(
            DocumentType.HARDSHIP_EVIDENCE,
            details.getRentIncreaseToCauseHardshipDocument(),
            ptCaseEntity
        );
    }

    @Transactional
    public void updateDocumentsForPropertyDetails(PropertyDetails details, PTCaseEntity ptCaseEntity) {
        updateSingleDocument(
            DocumentType.FLOOR_PLAN,
            details.getFloorPlanDocument(),
            ptCaseEntity
        );
        updateSingleDocument(
            DocumentType.OUTSIDE_PROPERTY,
            details.getOutsidePropertyDocument(),
            ptCaseEntity
        );
        updateSingleDocument(
            DocumentType.REPAIRS_EVIDENCE,
            details.getRepairsEvidenceDocument(),
            ptCaseEntity
        );
        updateMultipleDocuments(
            DocumentType.PROPERTY_ROOMS,
            details.getRoomsDocuments(),
            ptCaseEntity
        );
    }

    @Transactional
    protected void updateSingleDocument(
        DocumentType documentType,
        UploadedDocument uploadedDocument,
        PTCaseEntity ptCaseEntity
    ) {
        Optional<DocumentEntity> existing = getDocumentsOfTypeForCase(documentType, ptCaseEntity)
            .stream()
            .findFirst();

        if (uploadedDocument == null) {
            return;
        }

        DocumentEntity documentEntity = existing.orElseGet(DocumentEntity::new);
        saveDocument(documentEntity, uploadedDocument, documentType, ptCaseEntity);
    }

    @Transactional
    protected void updateMultipleDocuments(
        DocumentType documentType,
        List<ListValue<UploadedDocument>> uploadedDocuments,
        PTCaseEntity ptCaseEntity
    ) {
        if (uploadedDocuments == null) {
            return;
        }

        List<DocumentEntity> existingEntities = getDocumentsOfTypeForCase(documentType, ptCaseEntity);

        for (ListValue<UploadedDocument> item : uploadedDocuments) {
            UploadedDocument uploadedDocument = item == null ? null : item.getValue();
            if (uploadedDocument == null || uploadedDocument.getDocument() == null) {
                continue;
            }

            DocumentEntity documentEntity = existingEntities.stream()
                .filter(entity -> uploadedDocument.getDocument().getUrl().equals(entity.getUrl()))
                .findFirst()
                .orElseGet(DocumentEntity::new);

            saveDocument(documentEntity, uploadedDocument, documentType, ptCaseEntity);
        }
    }

    @Transactional
    public boolean deleteDocument(long documentId, long caseReference) {
        Optional<DocumentEntity> document =
            documentRepository.findByIdAndPtCaseCaseReference(documentId, caseReference);

        if (document.isEmpty()) {
            return false;
        }

        documentRepository.deleteByIdAndPtCaseCaseReference(documentId, caseReference);
        caseDocumentService.deleteDocument(document.get().getUrl());

        return true;
    }

    private void saveDocument(
        DocumentEntity entity,
        UploadedDocument uploadedDocument,
        DocumentType documentType,
        PTCaseEntity ptCaseEntity
    ) {
        Document document = uploadedDocument.getDocument();
        entity.setUrl(document.getUrl());
        entity.setFileName(document.getFilename());
        entity.setBinaryUrl(document.getBinaryUrl());
        entity.setSize(uploadedDocument.getSizeInBytes());
        entity.setContentType(uploadedDocument.getContentType());
        entity.setDocumentType(documentType);
        entity.setDescription(documentType.getLabel());
        entity.setPtCase(ptCaseEntity);
        entity.setCategoryId(CaseFileCategory.UNCATEGORISED_DOCUMENTS.getId());

        documentRepository.save(entity);
    }

    private List<DocumentEntity> getDocumentsOfTypeForCase(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .toList();
    }
}
