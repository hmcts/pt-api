package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CasePartyServiceTest {

    @Mock
    private CasePartyRepository casePartyRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private CasePartyRepresentativeService casePartyRepresentativeService;

    @BeforeEach
    void setUp() {
        lenient().when(casePartyRepository.save(any(CasePartyEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

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

        CasePartyEntity result = casePartyService.createApplicantCaseParty(ptCaseEntity, ptCase, idamId);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmailAddress()).isEqualTo("john.doe@example.com");
        assertThat(result.getPtCase().getCaseReference()).isEqualTo(caseReference);
        assertThat(result.getCasePartyRole()).isEqualTo(PartyRole.APPLICANT);
        assertThat(result.getIdamId()).isEqualTo(idamId);

        verify(casePartyRepository).save(any(CasePartyEntity.class));
        verify(addressService).updatePropertyAddress(any(PartyDetails.class), eq(result), eq(ptCaseEntity));
    }

    @Test
    @DisplayName("Should do nothing when landlord party details is null")
    void updateWithLandlordDetailsWhenLandlordNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        LandlordDetails landlordDetails = LandlordDetails.builder().build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        verify(casePartyRepository, never()).save(any());
        verify(addressService, never()).updatePartyAddress(any(), any());
    }

    @Test
    @DisplayName("Should save has-representative flag and update representative when YES")
    void updateWithLandlordDetailsRepresentativeYes() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(new ArrayList<>()).build();
        PartyDetails landlordParty = PartyDetails.builder().firstName("Landlord").build();
        PartyDetails repParty = PartyDetails.builder().firstName("Rep").build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlord(landlordParty)
            .hasRepresentative(YesNoNotSure.YES)
            .representative(repParty)
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        ArgumentCaptor<CasePartyEntity> captor = ArgumentCaptor.forClass(CasePartyEntity.class);
        verify(casePartyRepository, times(2)).save(captor.capture());
        CasePartyEntity savedLandlord = captor.getAllValues().get(1);
        assertThat(savedLandlord.getCasePartyRole()).isEqualTo(PartyRole.LANDLORD);
        assertThat(savedLandlord.getFirstName()).isEqualTo("Landlord");
        assertThat(savedLandlord.getHasRepresentative()).isEqualTo(YesNoNotSure.YES);
        verify(casePartyRepresentativeService).updateRepresentativeForLandlord(savedLandlord, repParty);
        verify(casePartyRepresentativeService, never()).removeRepresentativeForParty(any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = YesNoNotSure.class, names = {"NO", "NOT_SURE"})
    @DisplayName("Should remove the landlord representative when has-representative is NO or NOT_SURE")
    void updateWithLandlordDetailsRepresentativeRemoved(YesNoNotSure answer) {
        CasePartyEntity existingLandlord = CasePartyEntity.builder().casePartyRole(PartyRole.LANDLORD).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingLandlord)))
            .build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlord(PartyDetails.builder().firstName("Landlord").build())
            .hasRepresentative(answer)
            .representative(PartyDetails.builder().firstName("Ignored").build())
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        assertThat(existingLandlord.getHasRepresentative()).isEqualTo(answer);
        verify(casePartyRepresentativeService)
            .removeRepresentativeForParty(existingLandlord, RepresentativeType.LANDLORD_REPRESENTATIVE);
        verify(casePartyRepresentativeService, never()).updateRepresentativeForLandlord(any(), any());
    }

    @Test
    @DisplayName("Should leave representative untouched and keep existing flag when has representative is null")
    void updateWithLandlordDetailsRepresentativeNull() {
        CasePartyEntity existingLandlord = CasePartyEntity.builder()
            .casePartyRole(PartyRole.LANDLORD)
            .hasRepresentative(YesNoNotSure.YES)
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(new ArrayList<>(List.of(existingLandlord)))
            .build();
        LandlordDetails landlordDetails = LandlordDetails.builder()
            .landlord(PartyDetails.builder().firstName("Landlord").build())
            .build();

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        assertThat(existingLandlord.getHasRepresentative()).isEqualTo(YesNoNotSure.YES);
        verifyNoInteractions(casePartyRepresentativeService);
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
        CasePartyEntity existing = CasePartyEntity.builder().casePartyRole(PartyRole.LANDLORD).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(List.of(existing))
            .build();

        PartyDetails partyDetails = PartyDetails.builder()
            .firstName("Jane")
            .lastName("Smith")
            .emailAddress("jane@example.com")
            .phoneNumber("123456")
            .organisationName("Org")
            .build();

        casePartyService.updatePartyDetails(ptCaseEntity, partyDetails, PartyRole.LANDLORD);

        assertThat(existing.getFirstName()).isEqualTo("Jane");
        assertThat(existing.getLastName()).isEqualTo("Smith");
        assertThat(existing.getEmailAddress()).isEqualTo("jane@example.com");
        assertThat(existing.getPhoneNumber()).isEqualTo("123456");
        assertThat(existing.getOrganisationName()).isEqualTo("Org");
        verify(casePartyRepository).save(existing);
        verify(addressService).updatePartyAddress(partyDetails, existing);
        verify(addressService, never()).updatePropertyAddress(any(), any(), any());
    }

    @Test
    @DisplayName("Should remove the party, which also removes its address")
    void removeParty() {
        CasePartyEntity party = CasePartyEntity.builder().build();

        casePartyService.removeParty(party);

        verify(casePartyRepository).delete(party);
    }

    @Test
    @DisplayName("Should return party when matching role exists")
    void getPartyForCaseByRoleFound() {
        CasePartyEntity party = CasePartyEntity.builder().casePartyRole(PartyRole.APPLICANT).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(List.of(party)).build();

        Optional<CasePartyEntity> result = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT);

        assertThat(result).contains(party);
    }

    @Test
    @DisplayName("Should return empty when matching role not found")
    void getPartyForCaseByRoleNotFound() {
        CasePartyEntity party = CasePartyEntity.builder().casePartyRole(PartyRole.LANDLORD).build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().parties(List.of(party)).build();

        Optional<CasePartyEntity> result = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT);

        assertThat(result).isEmpty();
    }
}
