package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final CasePartyService casePartyService;
    private final CaseTypeService caseTypeService;
    private final PTCaseRepository ptCaseRepository;
    private final CaseApplicationRepository caseApplicationRepository;
    private final CasePartyRepository casePartyRepository;
    private final AddressRepository addressRepository;

    @Transactional
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

    @Transactional
    public void updateCase(long caseReference, PTCase ptCase) {
        CasePartyEntity caseParty = casePartyRepository.findFirstByPtCaseCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        caseParty.setFirstName(ptCase.getApplicantFirstName());
        caseParty.setLastName(ptCase.getApplicantLastName());
        caseParty.setEmailAddress(ptCase.getEmail());
        casePartyRepository.save(caseParty);

        caseParty.getAddresses().stream().findFirst().ifPresent(address -> {
            address.setPostcode(ptCase.getPostcode());
            addressRepository.save(address);
        });
    }
}
