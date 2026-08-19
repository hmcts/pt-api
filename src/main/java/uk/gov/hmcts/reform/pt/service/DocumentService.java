package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.reform.pt.ccd.domain.CaseFileCategory;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.UploadedDocument;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    @Transactional
    public void updateDocumentsForNoticeOfRentChange(NoticeOfRentIncreaseDetails details, PTCaseEntity ptCaseEntity) {
        updateDocument(
            DocumentType.NEW_RENT_INCREASE_NOTICE,
            details.getLandlordNoticeProposingNewRentDocument(),
            ptCaseEntity
        );
        updateDocument(
            DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE,
            details.getNoticeNotLegallyValidDocument(),
            ptCaseEntity
        );
        updateDocument(
            DocumentType.HARDSHIP_EVIDENCE,
            details.getRentIncreaseToCauseHardshipDocument(),
            ptCaseEntity
        );
    }

    @Transactional
    protected void updateDocument(
        DocumentType documentType,
        UploadedDocument uploadedDocument,
        PTCaseEntity ptCaseEntity
    ) {
        DocumentEntity documentEntity = getDocumentEntityOfTypeForCase(documentType, ptCaseEntity).orElse(null);
        if (uploadedDocument == null) {
            if (documentEntity != null) {
                // unsure what we want to do here
                documentRepository.delete(documentEntity);
            }
            return;
        }

        if (documentEntity == null) {
            documentEntity = new DocumentEntity();
        }

        Document document = uploadedDocument.getDocument();
        documentEntity.setPtCase(ptCaseEntity);
        // todo documentEntity.setCaseApplication(ptCaseEntity.getParties());
        documentEntity.setUrl(document.getUrl());
        documentEntity.setFileName(document.getFilename());
        documentEntity.setBinaryUrl(document.getBinaryUrl());
        documentEntity.setDescription(documentType.getLabel());
        documentEntity.setDocumentType(documentType);
        documentEntity.setSize(uploadedDocument.getSizeInBytes());
        documentEntity.setContentType(uploadedDocument.getContentType());
        documentEntity.setCategoryId(CaseFileCategory.UNCATEGORISED_DOCUMENTS.getId());

        documentRepository.save(documentEntity);
    }

    private Optional<DocumentEntity> getDocumentEntityOfTypeForCase(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .findFirst();
    }
}
