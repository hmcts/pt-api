package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyRoleEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyAccessRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRoleRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CasePartyService {

    private final CasePartyRepository casePartyRepository;
    private final CasePartyAccessRepository casePartyAccessRepository;
    private final CasePartyRoleRepository casePartyRoleRepository;
    private final AddressService addressService;

    @Transactional
    public CasePartyEntity createApplicantCaseParty(PTCaseEntity ptCaseEntity, PTCase ptCase, UUID idamId) {
        CasePartyRoleEntity casePartyRole = getOrCreateCasePartyRole(PartyRole.APPLICANT);

        CasePartyEntity caseParty = CasePartyEntity.builder()
            .firstName(ptCase.getApplicantFirstName())
            .lastName(ptCase.getApplicantLastName())
            .emailAddress(ptCase.getEmail())
            .ptCase(ptCaseEntity)
            .role(casePartyRole)
            .build();
        casePartyRepository.save(caseParty);

        PartyDetails partyDetails = PartyDetails.builder()
            .postcode(ptCase.getPostcode())
            .build();
        addressService.updateAddress(partyDetails, caseParty, ptCaseEntity);

        CasePartyAccessEntity access = CasePartyAccessEntity.builder()
            .idamId(idamId)
            .party(caseParty)
            .build();
        casePartyAccessRepository.save(access);

        return caseParty;
    }

    @Transactional
    public void updateWithLandlordDetails(PTCaseEntity ptCaseEntity, LandlordDetails landlordDetails) {
        PartyDetails landlord = landlordDetails.getLandlordPartyDetails();
        if (landlord == null) {
            return;
        }
        updatePartyDetails(ptCaseEntity, landlord, PartyRole.LANDLORD);

        PartyDetails lettingAgent = landlordDetails.getLettingAgentPartyDetails();
        PartyDetails representative = landlordDetails.getRepresentativePartyDetails();

        switch (landlordDetails.getRepresentativeType()) {
            case LETTING_AGENT -> {
                updatePartyDetails(ptCaseEntity, lettingAgent, PartyRole.LETTING_AGENT);
                getPartyForCaseByRole(ptCaseEntity, PartyRole.LANDLORD_REPRESENTATIVE)
                    .ifPresent(this::removeParty);
            }
            case REPRESENTATIVE -> {
                updatePartyDetails(ptCaseEntity, representative, PartyRole.LANDLORD_REPRESENTATIVE);
                getPartyForCaseByRole(ptCaseEntity, PartyRole.LANDLORD)
                    .ifPresent(this::removeParty);
            }
            case LETTING_AGENT_AND_REPRESENTATIVE -> {
                updatePartyDetails(ptCaseEntity, lettingAgent, PartyRole.LETTING_AGENT);
                updatePartyDetails(ptCaseEntity, representative, PartyRole.LANDLORD_REPRESENTATIVE);
            }
            case NO_LETTING_AGENT_OR_REPRESENTATIVE, NOT_SURE -> {
                getPartyForCaseByRole(ptCaseEntity, PartyRole.LETTING_AGENT)
                    .ifPresent(this::removeParty);
                getPartyForCaseByRole(ptCaseEntity, PartyRole.LANDLORD_REPRESENTATIVE)
                    .ifPresent(this::removeParty);
            }
        }
    }

    @Transactional
    public void updatePartyDetails(PTCaseEntity ptCaseEntity, PartyDetails partyDetails, PartyRole role) {
        if (partyDetails == null) {
            return;
        }

        CasePartyEntity caseParty = getPartyForCaseByRole(ptCaseEntity, role)
            .orElse(new CasePartyEntity());
        caseParty.setPtCase(ptCaseEntity);
        caseParty.setRole(getOrCreateCasePartyRole(role));
        caseParty.setFirstName(partyDetails.getFirstName());
        caseParty.setLastName(partyDetails.getLastName());
        caseParty.setEmailAddress(partyDetails.getEmailAddress());
        caseParty.setPhoneNumber(partyDetails.getPhoneNumber());
        caseParty.setOrganisationName(partyDetails.getOrganisationName());
        caseParty.setReferenceNumber(partyDetails.getDxNumber());
        casePartyRepository.save(caseParty);

        addressService.updateAddress(partyDetails, caseParty, ptCaseEntity);
    }

    @Transactional
    public CasePartyRoleEntity getOrCreateCasePartyRole(PartyRole roleName) {
        return casePartyRoleRepository.findFirstByRoleName(roleName)
            .orElseGet(() -> casePartyRoleRepository.save(CasePartyRoleEntity.builder().roleName(roleName).build()));
    }

    @Transactional
    public void removeParty(CasePartyEntity casePartyEntity) {
        addressService.deleteAddressesForParty(casePartyEntity);
        casePartyRepository.delete(casePartyEntity);
    }

    public Optional<CasePartyEntity> getPartyForCaseByRole(PTCaseEntity ptCaseEntity, PartyRole roleName) {
        return ptCaseEntity.getParties().stream()
            .filter(caseParty -> caseParty.getRole().getRoleName() == roleName)
            .findFirst();
    }
}
