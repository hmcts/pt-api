package uk.gov.hmcts.reform.pt.ccd;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.CaseView;
import uk.gov.hmcts.ccd.sdk.CaseViewRequest;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.service.CasePartyService;
import uk.gov.hmcts.reform.pt.service.PTCaseService;
import uk.gov.hmcts.reform.pt.util.AddressFormatter;
import uk.gov.hmcts.reform.pt.util.CaseFormatter;

import static uk.gov.hmcts.reform.pt.util.AddressFormatter.COMMA_DELIMITER;

/**
 * Invoked by CCD to load PT cases under the decentralised model.
 */
@Component
@AllArgsConstructor
public class PTCaseView implements CaseView<PTCase, State> {
    private final PTCaseService ptCaseService;
    private final CasePartyService casePartyService;

    @Override
    @Transactional(readOnly = true)
    public PTCase getCase(CaseViewRequest<State> request) {
        PTCaseEntity ptCaseEntity = ptCaseService.getCase(request.caseRef());
        CasePartyEntity applicant = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT)
            .orElse(null);
        CasePartyEntity respondent = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.LANDLORD)
            .orElse(null);
        CasePropertyAddressEntity propertyAddress = ptCaseEntity.getPropertyAddress();
        PropertyDetails propertyDetails = getPropertyDetails(propertyAddress);

        return PTCase.builder()
            .applicantFirstName(applicant != null ? applicant.getFirstName() : null)
            .applicantLastName(applicant != null ? applicant.getLastName() : null)
            .formattedPropertyAddress(getFormattedPropertyAddress(propertyDetails))
            .formattedCaseName(CaseFormatter.formatCaseName(applicant, respondent))
            .landlordDetails(getLandlordDetails(ptCaseEntity, respondent))
            .propertyDetails(propertyDetails)
            .build();
    }

    private LandlordDetails getLandlordDetails(PTCaseEntity ptCaseEntity, CasePartyEntity respondent) {
        return respondent != null
            ? LandlordDetails.builder()
                .representativeType(ptCaseEntity.getLandlordType())
                .landlordPartyDetails(
                    PartyDetails.builder()
                        .firstName(respondent.getFirstName())
                        .lastName(respondent.getLastName())
                        .build())
                .build()
            : null;
    }

    private PropertyDetails getPropertyDetails(CasePropertyAddressEntity address) {
        return address != null
            ? PropertyDetails.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .postTown(address.getTownCity())
                .county(address.getCounty())
                .postcode(address.getPostcode())
                .build()
            : null;
    }

    private String getFormattedPropertyAddress(PropertyDetails propertyDetails) {
        return propertyDetails != null
            ? AddressFormatter.formatAddressWithoutCountry(propertyDetails, COMMA_DELIMITER)
            : null;
    }
}
