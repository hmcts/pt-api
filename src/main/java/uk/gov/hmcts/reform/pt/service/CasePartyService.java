package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyAccessRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyAddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CasePartyService {

    private final CasePartyRepository casePartyRepository;
    private final CasePartyAccessRepository casePartyAccessRepository;
    private final CasePartyAddressRepository casePartyAddressRepository;

    public Optional<CasePartyEntity> getCasePartyByIdamId(UUID idamId) {
        return casePartyRepository.findFirstByAccessIdamId(idamId);
    }

    public CasePartyEntity createCaseParty(PTCaseEntity ptCaseEntity, PTCase ptCase, UUID idamId) {
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .firstName(ptCase.getApplicantFirstName())
            .lastName(ptCase.getApplicantLastName())
            .emailAddress(ptCase.getEmail())
            .build();
        casePartyRepository.save(caseParty);

        AddressEntity address = AddressEntity.builder()
            .postcode(ptCase.getPostcode())
            .party(caseParty)
            .ptCase(ptCaseEntity)
            .build();
        casePartyAddressRepository.save(address);

        CasePartyAccessEntity access = CasePartyAccessEntity.builder()
            .idamId(idamId)
            .party(caseParty)
            .build();
        casePartyAccessRepository.save(access);

        return caseParty;
    }
}
