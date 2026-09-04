package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordRepresentativeType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CasePartyServiceTest {

    @Mock
    private CasePartyRepository casePartyRepository;

    @Mock
    private CasePartyAccessRepository casePartyAccessRepository;

    @Mock
    private CasePartyRoleRepository casePartyRoleRepository;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CasePartyService casePartyService;

    @Test
    @DisplayName("Should create CasePartyEntity and related entities")
    void createApplicantCaseParty() {
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .email("john.doe@example.com")
            .postcode("AB12 3CD")
            .build();
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234L;
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().caseReference(caseReference).build();
        CasePartyRoleEntity roleEntity = CasePartyRoleEntity.builder().roleName(PartyRole.APPLICANT).build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.APPLICANT)).thenReturn(Optional.of(roleEntity));

        CasePartyEntity result = casePartyService.createApplicantCaseParty(ptCaseEntity, ptCase, idamId);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmailAddress()).isEqualTo("john.doe@example.com");
        assertThat(result.getPtCase().getCaseReference()).isEqualTo(caseReference);
        assertThat(result.getRole()).isEqualTo(roleEntity);

        verify(casePartyRepository).save(any(CasePartyEntity.class));
        verify(addressService).updateAddress(any(PartyDetails.class), eq(result), eq(ptCaseEntity));
        verify(casePartyAccessRepository).save(any(CasePartyAccessEntity.class));
    }

    @Test
    @DisplayName("Should create new CasePartyRoleEntity when role not found")
    void getOrCreateCasePartyRoleWhenNotFound() {
        CasePartyRoleEntity savedRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.empty());
        when(casePartyRoleRepository.save(any(CasePartyRoleEntity.class))).thenReturn(savedRole);

        CasePartyRoleEntity result = casePartyService.getOrCreateCasePartyRole(PartyRole.LANDLORD);

        assertThat(result).isEqualTo(savedRole);
        verify(casePartyRoleRepository).save(any(CasePartyRoleEntity.class));
    }

    @Test
    @DisplayName("Should do nothing when landlord party details is null")
    void updateWithLandlordDetailsWhenLandlordNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        LandlordDetails landlordDetails = LandlordDetails.builder().build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(casePartyRepository, never()).save(any());
        verify(addressService, never()).updateAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Should update landlord and letting agent when representative type is LETTING_AGENT")
    void updateWithLandlordDetailsLettingAgent() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity lettingAgentRole = CasePartyRoleEntity.builder().roleName(PartyRole.LETTING_AGENT).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        CasePartyEntity existingRep = CasePartyEntity.builder().role(repRole).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingRep)))
            .build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(landlordRole));
        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LETTING_AGENT))
            .thenReturn(Optional.of(lettingAgentRole));

        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        PartyDetails lettingAgentParty = PartyDetails.builder().firstName("Agent").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordParty)
            .lettingAgentPartyDetails(lettingAgentParty)
            .representativeType(LandlordRepresentativeType.LETTING_AGENT)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(addressService).deleteAddressesForParty(existingRep);
        verify(casePartyRepository).delete(existingRep);
        verify(casePartyRepository, org.mockito.Mockito.times(2)).save(any(CasePartyEntity.class));
    }

    @Test
    @DisplayName("Should update landlord and representative when representative type is REPRESENTATIVE")
    void updateWithLandlordDetailsRepresentative() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        CasePartyEntity existingLandlord = CasePartyEntity.builder().role(landlordRole).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingLandlord)))
            .build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(landlordRole));
        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD_REPRESENTATIVE))
            .thenReturn(Optional.of(repRole));

        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        PartyDetails repParty = PartyDetails.builder().firstName("Rep").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordParty)
            .representativePartyDetails(repParty)
            .representativeType(LandlordRepresentativeType.REPRESENTATIVE)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(addressService).deleteAddressesForParty(existingLandlord);
        verify(casePartyRepository).delete(existingLandlord);
    }

    @Test
    @DisplayName("Should update both when representative type is LETTING_AGENT_AND_REPRESENTATIVE")
    void updateWithLandlordDetailsBoth() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity lettingAgentRole = CasePartyRoleEntity.builder().roleName(PartyRole.LETTING_AGENT).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(landlordRole));
        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LETTING_AGENT))
            .thenReturn(Optional.of(lettingAgentRole));
        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD_REPRESENTATIVE))
            .thenReturn(Optional.of(repRole));

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(new ArrayList<>()).build();
        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        PartyDetails agentParty = PartyDetails.builder().firstName("Agent").build();
        PartyDetails repParty = PartyDetails.builder().firstName("Rep").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordParty)
            .lettingAgentPartyDetails(agentParty)
            .representativePartyDetails(repParty)
            .representativeType(LandlordRepresentativeType.LETTING_AGENT_AND_REPRESENTATIVE)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(casePartyRepository, org.mockito.Mockito.times(3)).save(any(CasePartyEntity.class));
    }

    @Test
    @DisplayName("Should remove agent and rep when representative type is NO_LETTING_AGENT_OR_REPRESENTATIVE")
    void updateWithLandlordDetailsNone() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity agentRole = CasePartyRoleEntity.builder().roleName(PartyRole.LETTING_AGENT).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        CasePartyEntity existingAgent = CasePartyEntity.builder().role(agentRole).build();
        CasePartyEntity existingRep = CasePartyEntity.builder().role(repRole).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingAgent, existingRep)))
            .build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(landlordRole));

        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordParty)
            .representativeType(LandlordRepresentativeType.NO_LETTING_AGENT_OR_REPRESENTATIVE)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(casePartyRepository).delete(existingAgent);
        verify(casePartyRepository).delete(existingRep);
    }

    @Test
    @DisplayName("Should remove agent and rep when representative type is NOT_SURE")
    void updateWithLandlordDetailsNotSure() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity agentRole = CasePartyRoleEntity.builder().roleName(PartyRole.LETTING_AGENT).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        CasePartyEntity existingAgent = CasePartyEntity.builder().role(agentRole).build();
        CasePartyEntity existingRep = CasePartyEntity.builder().role(repRole).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingAgent, existingRep)))
            .build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(landlordRole));

        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlordPartyDetails(landlordParty)
            .representativeType(LandlordRepresentativeType.NOT_SURE)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(casePartyRepository).delete(existingAgent);
        verify(casePartyRepository).delete(existingRep);
    }

    @Test
    @DisplayName("Should do nothing when partyDetails is null in updatePartyDetails")
    void updatePartyDetailsWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        casePartyService.updatePartyDetails(ptCaseEntity, null, PartyRole.LANDLORD);

        verify(casePartyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing party details when party exists for role")
    void updatePartyDetailsWhenPartyExists() {
        CasePartyRoleEntity role = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyEntity existing = CasePartyEntity.builder().role(role).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(List.of(existing))
            .build();

        when(casePartyRoleRepository.findFirstByRoleName(PartyRole.LANDLORD)).thenReturn(Optional.of(role));

        PartyDetails partyDetails = PartyDetails.builder()
            .firstName("Jane")
            .lastName("Smith")
            .emailAddress("jane@example.com")
            .phoneNumber("123456")
            .organisationName("Org")
            .dxNumber("DX123")
            .build();

        casePartyService.updatePartyDetails(ptCaseEntity, partyDetails, PartyRole.LANDLORD);

        assertThat(existing.getFirstName()).isEqualTo("Jane");
        assertThat(existing.getLastName()).isEqualTo("Smith");
        assertThat(existing.getEmailAddress()).isEqualTo("jane@example.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("123456");
        assertThat(existing.getOrganisationName()).isEqualTo("Org");
        assertThat(existing.getReferenceNumber()).isEqualTo("DX123");
        verify(casePartyRepository).save(existing);
        verify(addressService).updateAddress(partyDetails, existing, ptCaseEntity);
    }

    @Test
    @DisplayName("Should remove party and delete addresses")
    void removeParty() {
        CasePartyEntity party = CasePartyEntity.builder().build();

        casePartyService.removeParty(party);

        verify(addressService).deleteAddressesForParty(party);
        verify(casePartyRepository).delete(party);
    }

    @Test
    @DisplayName("Should return party when matching role exists")
    void getPartyForCaseByRoleFound() {
        CasePartyRoleEntity role = CasePartyRoleEntity.builder().roleName(PartyRole.APPLICANT).build();
        CasePartyEntity party = CasePartyEntity.builder().role(role).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(List.of(party)).build();

        Optional<CasePartyEntity> result = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT);

        assertThat(result).contains(party);
    }

    @Test
    @DisplayName("Should return empty when matching role not found")
    void getPartyForCaseByRoleNotFound() {
        CasePartyRoleEntity role = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyEntity party = CasePartyEntity.builder().role(role).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(List.of(party)).build();

        Optional<CasePartyEntity> result = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT);

        assertThat(result).isEmpty();
    }
}
