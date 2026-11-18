package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;

import java.util.Optional;
import java.util.UUID;

import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.LANDLORD;
import static uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType.LANDLORD_REPRESENTATIVE;
import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class CasePartyService {

    private final CasePartyRepository casePartyRepository;
    private final AddressService addressService;
    private final CasePartyRepresentativeService casePartyRepresentativeService;

    @Transactional
    public CasePartyEntity createApplicantCaseParty(PTCaseEntity ptCaseEntity, PTCase ptCase, UUID idamId) {
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .firstName(ptCase.getApplicantFirstName())
            .lastName(ptCase.getApplicantLastName())
            .emailAddress(ptCase.getEmail())
            .ptCase(ptCaseEntity)
            .casePartyRole(APPLICANT)
            .idamId(idamId)
            .build();
        casePartyRepository.save(caseParty);

        PartyDetails partyDetails = PartyDetails.builder()
            .postcode(ptCase.getPostcode())
            .build();
        addressService.updatePropertyAddress(partyDetails, caseParty, ptCaseEntity);

        return caseParty;
    }

    @Transactional
    public void updateWithLandlordDetails(PTCaseEntity ptCaseEntity, LandlordDetails landlordDetails) {
        PartyDetails landlord = landlordDetails.getLandlord();
        if (landlord == null) {
            return;
        }
        CasePartyEntity landlordPartyEntity = updatePartyDetails(ptCaseEntity, landlord, LANDLORD);
        setIfNotNull(landlordDetails.getHasRepresentative(), landlordPartyEntity::setHasRepresentative);
        casePartyRepository.save(landlordPartyEntity);

        PartyDetails representative = landlordDetails.getRepresentative();
        switch (landlordDetails.getHasRepresentative()) {
            case YES ->
                casePartyRepresentativeService.updateRepresentativeForLandlord(landlordPartyEntity, representative);
            case NO, NOT_SURE ->
                casePartyRepresentativeService.removeRepresentativeForParty(
                    landlordPartyEntity, LANDLORD_REPRESENTATIVE);
        }
    }

    @Transactional
    public CasePartyEntity updatePartyDetails(PTCaseEntity ptCaseEntity, PartyDetails partyDetails, PartyRole role) {
        if (partyDetails == null) {
            return null;
        }

        CasePartyEntity caseParty = getPartyForCaseByRole(ptCaseEntity, role)
            .orElse(new CasePartyEntity());
        caseParty.setPtCase(ptCaseEntity);
        caseParty.setCasePartyRole(role);
        setIfNotNull(partyDetails.getFirstName(), caseParty::setFirstName);
        setIfNotNull(partyDetails.getLastName(), caseParty::setLastName);
        setIfNotNull(partyDetails.getEmailAddress(), caseParty::setEmailAddress);
        setIfNotNull(partyDetails.getPhoneNumber(), caseParty::setPhoneNumber);
        setIfNotNull(partyDetails.getOrganisationName(), caseParty::setOrganisationName);
        caseParty = casePartyRepository.save(caseParty);

        addressService.updatePartyAddress(partyDetails, caseParty);

        return caseParty;
    }

    @Transactional
    public void removeParty(CasePartyEntity casePartyEntity) {
        casePartyRepository.delete(casePartyEntity);
    }

    public Optional<CasePartyEntity> getPartyForCaseByRole(PTCaseEntity ptCaseEntity, PartyRole roleName) {
        return ptCaseEntity.getParties().stream()
            .filter(caseParty -> caseParty.getCasePartyRole() == roleName)
            .findFirst();
    }
}
