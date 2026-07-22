package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final CasePartyService casePartyService;
    private final CaseTypeService caseTypeService;
    private final PTCaseRepository ptCaseRepository;
    private final CaseApplicationRepository caseApplicationRepository;

    public void createCase(
        long caseReference,
        UUID userId,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();
        ptCaseRepository.save(ptCaseEntity);

        CasePartyEntity caseParty = casePartyService.getCasePartyByIdamId(userId)
            .orElseGet(() -> casePartyService.createCaseParty(ptCaseEntity, ptCase, userId));

        CaseTypeEntity caseType = caseTypeService.getCaseTypeOrCreateIfNotExists(ptCase.getApplicationType());

        CaseApplicationEntity application = CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(caseType)
            .build();
        caseApplicationRepository.save(application);
    }
}
