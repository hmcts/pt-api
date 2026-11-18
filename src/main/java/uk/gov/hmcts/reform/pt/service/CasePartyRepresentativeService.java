package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentativeEntity;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepresentativeRepository;
import uk.gov.hmcts.reform.pt.repository.RepresentativeTypeRepository;

import java.util.Optional;

import static uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType.LANDLORD_REPRESENTATIVE;
import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class CasePartyRepresentativeService {
    private final CasePartyRepresentativeRepository casePartyRepresentativeRepository;
    private final RepresentativeTypeRepository representativeTypeRepository;

    @Transactional
    public void updateRepresentativeForLandlord(CasePartyEntity representee, PartyDetails representativeDetails) {
        CasePartyRepresentativeEntity partyRepresentativeEntity =
            getRepresentativeForPartyByType(representee, LANDLORD_REPRESENTATIVE)
                .orElseGet(() -> {
                    CasePartyRepresentativeEntity representative = new CasePartyRepresentativeEntity();
                    representative.setParty(representee);
                    return representative;
                });

        RepresentativeTypeEntity representativeTypeEntity =
            getOrCreateRepresentativeType(LANDLORD_REPRESENTATIVE);
        partyRepresentativeEntity.setRepresentativeType(representativeTypeEntity);

        setIfNotNull(representativeDetails.getFirstName(), partyRepresentativeEntity::setFirstName);
        setIfNotNull(representativeDetails.getLastName(), partyRepresentativeEntity::setLastName);
        setIfNotNull(representativeDetails.getOrganisationName(), partyRepresentativeEntity::setOrganisationName);
        setIfNotNull(representativeDetails.getEmailAddress(), partyRepresentativeEntity::setEmailAddress);
        setIfNotNull(representativeDetails.getPhoneNumber(), partyRepresentativeEntity::setPhoneNumber);
        setIfNotNull(representativeDetails.getAddressLine1(), partyRepresentativeEntity::setAddressLine1);
        setIfNotNull(representativeDetails.getAddressLine2(), partyRepresentativeEntity::setAddressLine2);
        setIfNotNull(representativeDetails.getPostTown(), partyRepresentativeEntity::setTownCity);
        setIfNotNull(representativeDetails.getCounty(), partyRepresentativeEntity::setCounty);
        setIfNotNull(representativeDetails.getPostcode(), partyRepresentativeEntity::setPostcode);
        setIfNotNull(representativeDetails.getCountry(), partyRepresentativeEntity::setCountry);

        casePartyRepresentativeRepository.save(partyRepresentativeEntity);
    }

    @Transactional
    public void removeRepresentativeForParty(CasePartyEntity representee, RepresentativeType representativeType) {
        getRepresentativeForPartyByType(representee, representativeType)
            .ifPresent(casePartyRepresentativeRepository::delete);
    }

    @Transactional
    public RepresentativeTypeEntity getOrCreateRepresentativeType(RepresentativeType representativeType) {
        return representativeTypeRepository.findByTypeName(representativeType)
            .orElseGet(() -> representativeTypeRepository.save(new RepresentativeTypeEntity(representativeType)));
    }

    public Optional<CasePartyRepresentativeEntity> getRepresentativeForPartyByType(
        CasePartyEntity casePartyEntity, RepresentativeType representativeType) {
        return casePartyEntity.getRepresentatives().stream()
            .filter(representative ->
                        representative.getRepresentativeType().getTypeName() == representativeType)
            .findFirst();
        }
}
