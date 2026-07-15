package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddress;
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

    public Optional<CaseParty> getCasePartyByIdamId(UUID idamId) {
        return casePartyRepository.findFirstByAccessIdamId(idamId);
    }

    public CaseParty createCaseParty(PTCase ptCase, UUID idamId) {
        CaseParty caseParty = CaseParty.builder()
            .firstName(ptCase.getApplicantFirstName())
            .lastName(ptCase.getApplicantLastName())
            .emailAddress(ptCase.getEmail())
            .build();
        casePartyRepository.save(caseParty);

        CasePartyAddress address = CasePartyAddress.builder()
            .postcode(ptCase.getPostcode())
            .party(caseParty)
            .build();
        casePartyAddressRepository.save(address);

        CasePartyAccess access = CasePartyAccess.builder()
            .idamId(idamId)
            .party(caseParty)
            .build();
        casePartyAccessRepository.save(access);

        return caseParty;
    }
}
