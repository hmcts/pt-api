package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("Should update existing address when party has an address")
    void updateAddressWhenExisting() {
        AddressEntity existingAddress = AddressEntity.builder()
            .addressLine1("Old Line 1")
            .build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .addresses(new ArrayList<>(List.of(existingAddress)))
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        PartyDetails partyDetails = PartyDetails.builder()
            .addressLine1("New Line 1")
            .addressLine2("New Line 2")
            .postTown("Post town")
            .county("County")
            .postcode("AB12 3CD")
            .build();

        addressService.updateAddress(partyDetails, caseParty, ptCaseEntity);

        verify(addressRepository).save(existingAddress);
        assertThat(existingAddress.getAddressLine1()).isEqualTo("New Line 1");
        assertThat(existingAddress.getAddressLine2()).isEqualTo("New Line 2");
        assertThat(existingAddress.getPostTown()).isEqualTo("Post town");
        assertThat(existingAddress.getCounty()).isEqualTo("County");
        assertThat(existingAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(existingAddress.getParty()).isEqualTo(caseParty);
        assertThat(existingAddress.getPtCase()).isEqualTo(ptCaseEntity);
    }

    @Test
    @DisplayName("Should create and save new address when party has no existing address")
    void updateAddressWhenEmpty() {
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .addresses(new ArrayList<>())
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        PartyDetails partyDetails = PartyDetails.builder()
            .addressLine1("Line 1")
            .postTown("Post town")
            .postcode("AB12 3CD")
            .build();

        addressService.updateAddress(partyDetails, caseParty, ptCaseEntity);

        ArgumentCaptor<AddressEntity> captor = ArgumentCaptor.forClass(AddressEntity.class);
        verify(addressRepository).save(captor.capture());
        AddressEntity savedAddress = captor.getValue();

        assertThat(savedAddress.getAddressLine1()).isEqualTo("Line 1");
        assertThat(savedAddress.getPostTown()).isEqualTo("Post town");
        assertThat(savedAddress.getPostcode()).isEqualTo("AB12 3CD");
        assertThat(savedAddress.getParty()).isEqualTo(caseParty);
        assertThat(savedAddress.getPtCase()).isEqualTo(ptCaseEntity);
    }

    @Test
    @DisplayName("Should delete all addresses for a party")
    void deleteAddressesForParty() {
        List<AddressEntity> addresses = List.of(
            AddressEntity.builder().addressLine1("1").build(),
            AddressEntity.builder().addressLine1("2").build()
        );
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .addresses(addresses)
            .build();

        addressService.deleteAddressesForParty(caseParty);

        verify(addressRepository).deleteAll(addresses);
    }
}
