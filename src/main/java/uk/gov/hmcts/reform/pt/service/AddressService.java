package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddress;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddress;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyAddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePropertyAddressRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final CasePartyAddressRepository casePartyAddressRepository;
    private final CasePropertyAddressRepository casePropertyAddressRepository;

    @Transactional
    public void updatePropertyAddress(
        PartyDetails addressDetails,
        CasePartyEntity caseParty,
        PTCaseEntity ptCaseEntity
    ) {
        CasePropertyAddress propertyAddress = ptCaseEntity.getPropertyAddress();
        if (propertyAddress == null) {
            propertyAddress = new CasePropertyAddress();
            ptCaseEntity.setPropertyAddress(propertyAddress);
        }

        setIfNotNull(addressDetails.getAddressLine1(), propertyAddress::setAddressLine1);
        setIfNotNull(addressDetails.getAddressLine2(), propertyAddress::setAddressLine2);
        setIfNotNull(addressDetails.getPostTown(), propertyAddress::setTownCity);
        setIfNotNull(addressDetails.getCounty(), propertyAddress::setCounty);
        setIfNotNull(addressDetails.getPostcode(), propertyAddress::setPostcode);
        propertyAddress.setPtCase(ptCaseEntity);

        casePropertyAddressRepository.save(propertyAddress);

        updatePartyAddress(addressDetails, caseParty);
    }

    @Transactional
    public void updatePartyAddress(PartyDetails partyDetails, CasePartyEntity caseParty) {
        CasePartyAddress address = caseParty.getAddress();
        if (address == null) {
            address = new CasePartyAddress();
            caseParty.setAddress(address);
        }

        setIfNotNull(partyDetails.getAddressLine1(), address::setAddressLine1);
        setIfNotNull(partyDetails.getAddressLine2(), address::setAddressLine2);
        setIfNotNull(partyDetails.getPostTown(), address::setTownCity);
        setIfNotNull(partyDetails.getCounty(), address::setCounty);
        setIfNotNull(partyDetails.getPostcode(), address::setPostcode);
        address.setParty(caseParty);

        casePartyAddressRepository.save(address);
    }
}
