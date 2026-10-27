package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyAddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePropertyAddressRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private CasePartyAddressRepository casePartyAddressRepository;

    @Mock
    private CasePropertyAddressRepository casePropertyAddressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("Should create a property address and a party address when neither exists")
    void updatePropertyAddressCreatesBothAddresses() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        PartyDetails addressDetails = PartyDetails.builder()
            .addressLine1("Line 1")
            .addressLine2("Line 2")
            .postTown("Post town")
            .county("County")
            .postcode("AB12 3CD")
            .build();

        addressService.updatePropertyAddress(addressDetails, caseParty, ptCaseEntity);

        ArgumentCaptor<CasePropertyAddressEntity> propertyCaptor =
            ArgumentCaptor.forClass(CasePropertyAddressEntity.class);
        verify(casePropertyAddressRepository).save(propertyCaptor.capture());
        CasePropertyAddressEntity propertyAddress = propertyCaptor.getValue();

        assertThat(propertyAddress.getAddressLine1()).isEqualTo("Line 1");
        assertThat(propertyAddress.getAddressLine2()).isEqualTo("Line 2");
        assertThat(propertyAddress.getTownCity()).isEqualTo("Post town");
        assertThat(propertyAddress.getCounty()).isEqualTo("County");
        assertThat(propertyAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(propertyAddress.getPtCase()).isEqualTo(ptCaseEntity);
        assertThat(ptCaseEntity.getPropertyAddress()).isEqualTo(propertyAddress);

        ArgumentCaptor<CasePartyAddressEntity> partyCaptor = ArgumentCaptor.forClass(CasePartyAddressEntity.class);
        verify(casePartyAddressRepository).save(partyCaptor.capture());
        CasePartyAddressEntity partyAddress = partyCaptor.getValue();

        assertThat(partyAddress.getAddressLine1()).isEqualTo("Line 1");
        assertThat(partyAddress.getAddressLine2()).isEqualTo("Line 2");
        assertThat(partyAddress.getTownCity()).isEqualTo("Post town");
        assertThat(partyAddress.getCounty()).isEqualTo("County");
        assertThat(partyAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(partyAddress.getParty()).isEqualTo(caseParty);
        assertThat(caseParty.getAddress()).isEqualTo(partyAddress);
    }

    @Test
    @DisplayName("Should update the existing property address and party address")
    void updatePropertyAddressUpdatesExistingAddresses() {
        CasePropertyAddressEntity existingPropertyAddress = CasePropertyAddressEntity.builder()
            .addressLine1("Old property line 1")
            .postcode("OLD 1AA")
            .build();
        CasePartyAddressEntity existingPartyAddress = CasePartyAddressEntity.builder()
            .addressLine1("Old party line 1")
            .postcode("OLD 1AA")
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .propertyAddress(existingPropertyAddress)
            .build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .address(existingPartyAddress)
            .build();

        PartyDetails addressDetails = PartyDetails.builder()
            .addressLine1("New line 1")
            .postcode("AB12 3CD")
            .build();

        addressService.updatePropertyAddress(addressDetails, caseParty, ptCaseEntity);

        verify(casePropertyAddressRepository).save(existingPropertyAddress);
        assertThat(existingPropertyAddress.getAddressLine1()).isEqualTo("New line 1");
        assertThat(existingPropertyAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(existingPropertyAddress.getPtCase()).isEqualTo(ptCaseEntity);

        verify(casePartyAddressRepository).save(existingPartyAddress);
        assertThat(existingPartyAddress.getAddressLine1()).isEqualTo("New line 1");
        assertThat(existingPartyAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(existingPartyAddress.getParty()).isEqualTo(caseParty);
    }

    @Test
    @DisplayName("Should create only a party address when the party has no address")
    void updatePartyAddressCreatesPartyAddressOnly() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();

        PartyDetails partyDetails = PartyDetails.builder()
            .addressLine1("Line 1")
            .postTown("Post town")
            .postcode("AB12 3CD")
            .build();

        addressService.updatePartyAddress(partyDetails, caseParty);

        ArgumentCaptor<CasePartyAddressEntity> captor = ArgumentCaptor.forClass(CasePartyAddressEntity.class);
        verify(casePartyAddressRepository).save(captor.capture());
        CasePartyAddressEntity savedAddress = captor.getValue();

        assertThat(savedAddress.getAddressLine1()).isEqualTo("Line 1");
        assertThat(savedAddress.getTownCity()).isEqualTo("Post town");
        assertThat(savedAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(savedAddress.getParty()).isEqualTo(caseParty);
        assertThat(caseParty.getAddress()).isEqualTo(savedAddress);
        verify(casePropertyAddressRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update the existing party address without touching the property address")
    void updatePartyAddressUpdatesExistingPartyAddress() {
        CasePartyAddressEntity existingAddress = CasePartyAddressEntity.builder()
            .addressLine1("Old line 1")
            .build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .address(existingAddress)
            .build();

        PartyDetails partyDetails = PartyDetails.builder()
            .addressLine1("New line 1")
            .addressLine2("New line 2")
            .postTown("Post town")
            .county("County")
            .postcode("AB12 3CD")
            .build();

        addressService.updatePartyAddress(partyDetails, caseParty);

        verify(casePartyAddressRepository).save(existingAddress);
        assertThat(existingAddress.getAddressLine1()).isEqualTo("New line 1");
        assertThat(existingAddress.getAddressLine2()).isEqualTo("New line 2");
        assertThat(existingAddress.getTownCity()).isEqualTo("Post town");
        assertThat(existingAddress.getCounty()).isEqualTo("County");
        assertThat(existingAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(existingAddress.getParty()).isEqualTo(caseParty);
        verify(casePropertyAddressRepository, never()).save(any());
    }
}
