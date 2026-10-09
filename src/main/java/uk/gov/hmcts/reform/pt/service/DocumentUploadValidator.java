package uk.gov.hmcts.reform.pt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static uk.gov.hmcts.reform.pt.service.document.DocumentValidationErrors.REMOVE_FILE_FIRST;
import static uk.gov.hmcts.reform.pt.service.document.DocumentValidationErrors.TOTAL_TOO_LARGE;

@Component
public class DocumentUploadValidator {
    private final DocumentRepository documentRepository;
    private final long maxTotalBytes;

    public DocumentUploadValidator(
        DocumentRepository documentRepository,
        @Value("${documents.max-total-size-mb:300}") long maxTotalSizeMb
    ) {
        this.documentRepository = documentRepository;
        this.maxTotalBytes = maxTotalSizeMb * 1024 * 1024;
    }

    public Map<Long, String> snapshot(long caseReference) {
        return documentRepository.findAllByPtCaseCaseReference(caseReference).stream()
            .collect(toMap(DocumentEntity::getId, DocumentEntity::getUrl));
    }

    public List<String> errors(long caseReference, Map<Long, String> urlsBefore) {
        List<DocumentEntity> documents = documentRepository.findAllByPtCaseCaseReference(caseReference);
        List<String> errors = new ArrayList<>();

        boolean replacedExisting = documents.stream()
            .anyMatch(doc -> urlsBefore.containsKey(doc.getId())
                && !Objects.equals(urlsBefore.get(doc.getId()), doc.getUrl()));
        if (replacedExisting) {
            errors.add(REMOVE_FILE_FIRST.value());
        }

        Set<DocumentType> changedTypes = documents.stream()
            .filter(doc -> !Objects.equals(urlsBefore.get(doc.getId()), doc.getUrl()))
            .map(DocumentEntity::getDocumentType)
            .collect(toSet());
        boolean overLimit = documents.stream()
            .filter(doc -> changedTypes.contains(doc.getDocumentType()))
            .collect(groupingBy(
                DocumentEntity::getDocumentType,
                summingLong(doc -> Optional.ofNullable(doc.getSize()).orElse(0L))
            ))
            .values().stream()
            .anyMatch(total -> total > maxTotalBytes);
        if (overLimit) {
            errors.add(TOTAL_TOO_LARGE.value());
        }

        return errors;
    }
}
