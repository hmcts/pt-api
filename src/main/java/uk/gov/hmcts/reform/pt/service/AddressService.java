package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.entity.AbstractAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
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
        CasePropertyAddressEntity propertyAddress = ptCaseEntity.getPropertyAddress();
        if (propertyAddress == null) {
            propertyAddress = new CasePropertyAddressEntity();
            ptCaseEntity.setPropertyAddress(propertyAddress);
        }

        applyAddressDetails(addressDetails, propertyAddress);
        propertyAddress.setPtCase(ptCaseEntity);

        casePropertyAddressRepository.save(propertyAddress);

        updatePartyAddress(addressDetails, caseParty);
    }

    @Transactional
    public void updatePartyAddress(PartyDetails partyDetails, CasePartyEntity caseParty) {
        CasePartyAddressEntity address = caseParty.getAddress();
        if (address == null) {
            address = new CasePartyAddressEntity();
            caseParty.setAddress(address);
        }

        applyAddressDetails(partyDetails, address);
        address.setParty(caseParty);

        casePartyAddressRepository.save(address);
    }

    private static void applyAddressDetails(PartyDetails addressDetails, AbstractAddressEntity address) {
        setIfNotNull(addressDetails.getAddressLine1(), address::setAddressLine1);
        setIfNotNull(addressDetails.getAddressLine2(), address::setAddressLine2);
        setIfNotNull(addressDetails.getPostTown(), address::setTownCity);
        setIfNotNull(addressDetails.getCounty(), address::setCounty);
        setIfNotNull(addressDetails.getPostcode(), address::setPostcode);
    }
}
