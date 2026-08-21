package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.UploadedDocument;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

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
            details.getPropertyRoomsDocuments(),
            ptCaseEntity
        );
    }

    /**
     * For document types where the case can only ever hold one document of that type.
     * Overwrites the existing entity if present, otherwise creates a new one.
     */
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
            // todo unsure what we want to do here
            // existing.ifPresent(documentEntity -> {
            //     documentRepository.delete(documentEntity);
            // });
            return;
        }

        DocumentEntity documentEntity = existing.orElseGet(DocumentEntity::new);
        saveDocument(documentEntity, uploadedDocument, documentType, ptCaseEntity);
    }

    /**
     * For document types where the case can hold many documents of that type.
     * Reconciles the incoming list against what is already stored, matched by document URL
     * existing documents still present are updated in place, ones no longer present are to be removed.
     */
    @Transactional
    protected void updateMultipleDocuments(
        DocumentType documentType,
        List<UploadedDocument> uploadedDocuments,
        PTCaseEntity ptCaseEntity
    ) {
        List<DocumentEntity> existingEntities = getDocumentsOfTypeForCase(documentType, ptCaseEntity);

        // todo unsure what we want to do here
        // Set<String> incomingUrls = uploadedDocuments.stream()
        //     .map(uploadedDocument -> uploadedDocument.getDocument().getUrl())
        //     .collect(Collectors.toSet());

        // existingEntities.stream()
        //    .filter(documentEntity -> !incomingUrls.contains(documentEntity.getUrl()))
        //    .forEach(documentEntity -> {
        //        documentRepository.delete(documentEntity);
        //    });

        for (UploadedDocument uploadedDocument : uploadedDocuments) {
            DocumentEntity documentEntity = existingEntities.stream()
                .filter(entity -> entity.getUrl().equals(uploadedDocument.getDocument().getUrl()))
                .findFirst()
                .orElseGet(DocumentEntity::new);

            saveDocument(documentEntity, uploadedDocument, documentType, ptCaseEntity);
        }
    }

    private void saveDocument(
        DocumentEntity entity,
        UploadedDocument uploadedDocument,
        DocumentType documentType,
        PTCaseEntity ptCaseEntity
    ) {
        entity.setPtCase(ptCaseEntity);
        // todo entity.setCaseApplication(ptCaseEntity.getParties());
        entity.setCategoryId(CaseFileCategory.UNCATEGORISED_DOCUMENTS.getId());

        Document document = uploadedDocument.getDocument();
        entity.setUrl(document.getUrl());
        entity.setFileName(document.getFilename());
        entity.setBinaryUrl(document.getBinaryUrl());

        entity.setSize(uploadedDocument.getSizeInBytes());
        entity.setContentType(uploadedDocument.getContentType());

        entity.setDocumentType(documentType);
        entity.setDescription(documentType.getLabel());

        documentRepository.save(entity);
    }

    private List<DocumentEntity> getDocumentsOfTypeForCase(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .toList();
    }
}
