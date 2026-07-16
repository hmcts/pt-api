package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.repository.CaseTypeRepository;

@Service
@RequiredArgsConstructor
public class CaseTypeService {

    private final CaseTypeRepository caseTypeRepository;

    @Transactional
    public CaseTypeEntity getCaseTypeOrCreateIfNotExists(ApplicationType applicationType) {
        return caseTypeRepository.findFirstByApplicationTypeName(applicationType)
            .orElseGet(() -> createCaseType(applicationType));
    }

    @Transactional
    public CaseTypeEntity createCaseType(ApplicationType applicationType) {
        CaseTypeEntity caseType = CaseTypeEntity.builder()
            .applicationTypeName(applicationType)
            .build();
        return caseTypeRepository.save(caseType);
    }
}
