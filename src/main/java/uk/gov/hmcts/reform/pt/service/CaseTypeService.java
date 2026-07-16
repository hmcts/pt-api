package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.repository.CaseTypeRepository;

@Service
@RequiredArgsConstructor
public class CaseTypeService {

    private final CaseTypeRepository caseTypeRepository;

    @Transactional
    public CaseTypeEntity getCaseTypeOrCreateIfNotExists(String typeName) {
        return caseTypeRepository.findFirstByApplicationTypeName(typeName)
            .orElseGet(() -> createCaseType(typeName));
    }

    @Transactional
    public CaseTypeEntity createCaseType(String typeName) {
        CaseTypeEntity caseType = CaseTypeEntity.builder()
            .applicationTypeName(typeName)
            .build();
        return caseTypeRepository.save(caseType);
    }
}
