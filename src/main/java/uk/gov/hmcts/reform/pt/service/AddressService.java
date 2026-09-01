package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional
    public void updateAddress(PartyDetails partyDetails, CasePartyEntity caseParty, PTCaseEntity ptCaseEntity) {
        AddressEntity address = caseParty.getAddresses().stream().findFirst().orElse(new AddressEntity());
        address.setAddressLine1(partyDetails.getAddressLine1());
        address.setAddressLine2(partyDetails.getAddressLine2());
        address.setPostTown(partyDetails.getPostTown());
        address.setCounty(partyDetails.getCounty());
        address.setPostcode(partyDetails.getPostcode());
        address.setParty(caseParty);
        address.setPtCase(ptCaseEntity);
        addressRepository.save(address);
    }

    @Transactional
    public void deleteAddressesForParty(CasePartyEntity caseParty) {
        addressRepository.deleteAll(caseParty.getAddresses());
    }
}
