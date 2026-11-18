package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentativeEntity;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepresentativeRepository;
import uk.gov.hmcts.reform.pt.repository.RepresentativeTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType.LANDLORD_REPRESENTATIVE;

@ExtendWith(MockitoExtension.class)
class CasePartyRepresentativeServiceTest {

    @Mock
    private CasePartyRepresentativeRepository casePartyRepresentativeRepository;

    @Mock
    private RepresentativeTypeRepository representativeTypeRepository;

    @InjectMocks
    private CasePartyRepresentativeService service;

    private RepresentativeTypeEntity landlordRepType;

    @BeforeEach
    void setUp() {
        landlordRepType = RepresentativeTypeEntity.builder().typeName(LANDLORD_REPRESENTATIVE).build();
    }

    private static CasePartyRepresentativeEntity representativeOfType(RepresentativeTypeEntity type) {
        return CasePartyRepresentativeEntity.builder().representativeType(type).build();
    }

    private static CasePartyEntity partyWith(CasePartyRepresentativeEntity... representatives) {
        return CasePartyEntity.builder().representatives(new ArrayList<>(List.of(representatives))).build();
    }

    @Test
    @DisplayName("Should do nothing when representative details are null")
    void updateRepresentativeForLandlordWhenNull() {
        service.updateRepresentativeForLandlord(CasePartyEntity.builder().build(), null);

        verify(casePartyRepresentativeRepository, never()).save(any());
        verify(representativeTypeRepository, never()).findByTypeName(any());
    }

    @Test
    @DisplayName("Should create a new representative with all details when none exists")
    void updateRepresentativeForLandlordCreatesNew() {
        CasePartyEntity landlord = CasePartyEntity.builder().build();
        when(representativeTypeRepository.findByTypeName(LANDLORD_REPRESENTATIVE))
            .thenReturn(Optional.of(landlordRepType));

        PartyDetails details = PartyDetails.builder()
            .firstName("Rep")
            .lastName("Jones")
            .organisationName("Rep Ltd")
            .emailAddress("rep@example.com")
            .phoneNumber("0123456789")
            .addressLine1("1 Rep Street")
            .addressLine2("Rep Area")
            .postTown("Leeds")
            .county("West Yorkshire")
            .postcode("LS1 1AA")
            .country("UK")
            .build();

        service.updateRepresentativeForLandlord(landlord, details);

        ArgumentCaptor<CasePartyRepresentativeEntity> captor =
            ArgumentCaptor.forClass(CasePartyRepresentativeEntity.class);
        verify(casePartyRepresentativeRepository).save(captor.capture());
        CasePartyRepresentativeEntity saved = captor.getValue();
        assertThat(saved.getParty()).isSameAs(landlord);
        assertThat(saved.getRepresentativeType()).isSameAs(landlordRepType);
        assertThat(saved.getFirstName()).isEqualTo("Rep");
        assertThat(saved.getLastName()).isEqualTo("Jones");
        assertThat(saved.getOrganisationName()).isEqualTo("Rep Ltd");
        assertThat(saved.getEmailAddress()).isEqualTo("rep@example.com");
        assertThat(saved.getPhoneNumber()).isEqualTo("0123456789");
        assertThat(saved.getAddressLine1()).isEqualTo("1 Rep Street");
        assertThat(saved.getAddressLine2()).isEqualTo("Rep Area");
        assertThat(saved.getTownCity()).isEqualTo("Leeds");
        assertThat(saved.getCounty()).isEqualTo("West Yorkshire");
        assertThat(saved.getPostcode()).isEqualTo("LS1 1AA");
        assertThat(saved.getCountry()).isEqualTo("UK");
    }

    @Test
    @DisplayName("Should update the existing representative and keep fields not supplied")
    void updateRepresentativeForLandlordUpdatesExisting() {
        CasePartyRepresentativeEntity existing = representativeOfType(landlordRepType);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setCounty("Old County");
        CasePartyEntity landlord = partyWith(existing);
        when(representativeTypeRepository.findByTypeName(LANDLORD_REPRESENTATIVE))
            .thenReturn(Optional.of(landlordRepType));

        service.updateRepresentativeForLandlord(landlord, PartyDetails.builder().firstName("New").build());

        verify(casePartyRepresentativeRepository).save(existing);
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getLastName()).isEqualTo("Name");
        assertThat(existing.getCounty()).isEqualTo("Old County");
    }

    @Test
    @DisplayName("Should delete the landlord representative when present")
    void removeRepresentativeForPartyDeletes() {
        CasePartyRepresentativeEntity existing = representativeOfType(landlordRepType);

        service.removeRepresentativeForParty(partyWith(existing), LANDLORD_REPRESENTATIVE);

        verify(casePartyRepresentativeRepository).delete(existing);
    }

    @Test
    @DisplayName("Should not delete anything when the party has no representative")
    void removeRepresentativeForPartyNoneExists() {
        service.removeRepresentativeForParty(partyWith(), LANDLORD_REPRESENTATIVE);

        verify(casePartyRepresentativeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return existing representative type without saving")
    void getOrCreateRepresentativeTypeExisting() {
        when(representativeTypeRepository.findByTypeName(LANDLORD_REPRESENTATIVE))
            .thenReturn(Optional.of(landlordRepType));

        assertThat(service.getOrCreateRepresentativeType(LANDLORD_REPRESENTATIVE)).isSameAs(landlordRepType);

        verify(representativeTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create and save representative type when it does not exist")
    void getOrCreateRepresentativeTypeCreates() {
        when(representativeTypeRepository.findByTypeName(LANDLORD_REPRESENTATIVE)).thenReturn(Optional.empty());
        when(representativeTypeRepository.save(any(RepresentativeTypeEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        RepresentativeTypeEntity result = service.getOrCreateRepresentativeType(LANDLORD_REPRESENTATIVE);

        assertThat(result.getTypeName()).isEqualTo(LANDLORD_REPRESENTATIVE);
        verify(representativeTypeRepository).save(any(RepresentativeTypeEntity.class));
    }

    @Test
    @DisplayName("Should find representative by type and return empty when absent")
    void getRepresentativeForPartyByType() {
        CasePartyRepresentativeEntity existing = representativeOfType(landlordRepType);

        assertThat(service.getRepresentativeForPartyByType(partyWith(existing), LANDLORD_REPRESENTATIVE))
            .contains(existing);
        assertThat(service.getRepresentativeForPartyByType(partyWith(), LANDLORD_REPRESENTATIVE)).isEmpty();
    }
}
