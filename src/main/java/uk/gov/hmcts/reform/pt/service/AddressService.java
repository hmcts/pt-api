package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional
    public void updateAddress(PartyDetails partyDetails, CasePartyEntity caseParty, PTCaseEntity ptCaseEntity) {
        AddressEntity address = caseParty.getAddresses().stream().findFirst().orElse(new AddressEntity());

        setIfNotNull(partyDetails.getAddressLine1(), address::setAddressLine1);
        setIfNotNull(partyDetails.getAddressLine2(), address::setAddressLine2);
        setIfNotNull(partyDetails.getPostTown(), address::setPostTown);
        setIfNotNull(partyDetails.getCounty(), address::setCounty);
        setIfNotNull(partyDetails.getPostcode(), address::setPostcode);
        address.setParty(caseParty);
        address.setPtCase(ptCaseEntity);

        addressRepository.save(address);
    }

    @Transactional
    public void deleteAddressesForParty(CasePartyEntity caseParty) {
        addressRepository.deleteAll(caseParty.getAddresses());
    }
}
