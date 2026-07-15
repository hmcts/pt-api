package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.CaseType;
import uk.gov.hmcts.reform.pt.repository.CaseTypeRepository;

@Service
@RequiredArgsConstructor
public class CaseTypeService {

    private final CaseTypeRepository caseTypeRepository;

    @Transactional
    public CaseType getCaseTypeOrCreateIfNotExists(String typeName) {
        return caseTypeRepository.findFirstByApplicationTypeName(typeName)
            .orElseGet(() -> createCaseType(typeName));
    }

    @Transactional
    public CaseType createCaseType(String typeName) {
        CaseType caseType = CaseType.builder()
            .applicationTypeName(typeName)
            .build();
        return caseTypeRepository.save(caseType);
    }
}
