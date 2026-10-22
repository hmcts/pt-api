package uk.gov.hmcts.reform.pt.ccd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.CaseViewRequest;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordRepresentativeType;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.service.CasePartyService;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PTCaseViewTest {

    private static final long CASE_REF = 1234567890123456L;

    @Mock
    private PTCaseService ptCaseService;

    @Mock
    private CasePartyService casePartyService;

    @InjectMocks
    private PTCaseView ptCaseView;

    @Test
    void shouldMapApplicantLandlordAndPropertyAddress() {
        CasePartyEntity applicant = applicant(List.of(address()));
        CasePartyEntity landlord = party("John", "Smith");

        PTCase result = getCase(LandlordRepresentativeType.REPRESENTATIVE,
                                Optional.of(applicant), Optional.of(landlord));

        assertThat(result.getApplicantFirstName()).isEqualTo("Jane");
        assertThat(result.getApplicantLastName()).isEqualTo("Doe");
        assertThat(result.getFormattedPropertyAddress())
            .isEqualTo("10 Downing Street, Westminster, London, Greater London, SW1A 2AA");
        assertThat(result.getFormattedCaseName()).isEqualTo("Jane Doe vs John Smith");
        assertThat(result.getPropertyDetails())
            .satisfies(address -> {
                assertThat(address.getAddressLine1()).isEqualTo("10 Downing Street");
                assertThat(address.getAddressLine2()).isEqualTo("Westminster");
                assertThat(address.getPostTown()).isEqualTo("London");
                assertThat(address.getCounty()).isEqualTo("Greater London");
                assertThat(address.getPostcode()).isEqualTo("SW1A 2AA");
            });
        assertLandlord(result, LandlordRepresentativeType.REPRESENTATIVE);
    }

    @Test
    void shouldMapApplicantWithoutAddresses() {
        PTCase result = getCase(LandlordRepresentativeType.LETTING_AGENT,
                                Optional.of(applicant(List.of())), Optional.of(party("John", "Smith")));

        assertThat(result.getApplicantFirstName()).isEqualTo("Jane");
        assertThat(result.getApplicantLastName()).isEqualTo("Doe");
        assertThat(result.getFormattedPropertyAddress()).isNull();
        assertThat(result.getFormattedCaseName()).isEqualTo("Jane Doe vs John Smith");
        assertThat(result.getPropertyDetails()).isNull();
        assertLandlord(result, LandlordRepresentativeType.LETTING_AGENT);
    }

    @Test
    void shouldMapCaseWithoutApplicant() {
        PTCase result = getCase(LandlordRepresentativeType.REPRESENTATIVE,
                                Optional.empty(), Optional.of(party("John", "Smith")));

        assertThat(result.getApplicantFirstName()).isNull();
        assertThat(result.getApplicantLastName()).isNull();
        assertThat(result.getFormattedPropertyAddress()).isNull();
        assertThat(result.getFormattedCaseName()).isEmpty();
        assertThat(result.getPropertyDetails()).isNull();
        assertLandlord(result, LandlordRepresentativeType.REPRESENTATIVE);
    }

    @Test
    void shouldMapCaseWithoutLandlord() {
        PTCase result = getCase(null,
                                Optional.of(applicant(List.of(address()))), Optional.empty());

        assertThat(result.getApplicantFirstName()).isEqualTo("Jane");
        assertThat(result.getApplicantLastName()).isEqualTo("Doe");
        assertThat(result.getFormattedPropertyAddress())
            .isEqualTo("10 Downing Street, Westminster, London, Greater London, SW1A 2AA");
        assertThat(result.getFormattedCaseName()).isEmpty();
        assertThat(result.getLandlordDetails()).isNull();
        assertThat(result.getPropertyDetails())
            .satisfies(address -> {
                assertThat(address.getAddressLine1()).isEqualTo("10 Downing Street");
                assertThat(address.getPostcode()).isEqualTo("SW1A 2AA");
            });
    }

    @Test
    void shouldMapCaseWithoutApplicantOrLandlord() {
        var result = getCase(null, Optional.empty(), Optional.empty());

        assertThat(result.getApplicantFirstName()).isNull();
        assertThat(result.getApplicantLastName()).isNull();
        assertThat(result.getFormattedPropertyAddress()).isNull();
        assertThat(result.getFormattedCaseName()).isEmpty();
        assertThat(result.getLandlordDetails()).isNull();
        assertThat(result.getPropertyDetails()).isNull();
    }

    @SuppressWarnings("unchecked")
    private PTCase getCase(
        LandlordRepresentativeType representativeType,
        Optional<CasePartyEntity> applicant,
        Optional<CasePartyEntity> landlord
    ) {
        CaseViewRequest<State> request = mock(CaseViewRequest.class);
        when(request.caseRef()).thenReturn(CASE_REF);

        var entity = PTCaseEntity.builder()
            .caseReference(CASE_REF)
            .landlordType(representativeType)
            .build();

        when(ptCaseService.getCase(CASE_REF)).thenReturn(entity);
        when(casePartyService.getPartyForCaseByRole(entity, PartyRole.APPLICANT))
            .thenReturn(applicant);
        when(casePartyService.getPartyForCaseByRole(entity, PartyRole.LANDLORD))
            .thenReturn(landlord);

        return ptCaseView.getCase(request);
    }

    private static CasePartyEntity applicant(List<AddressEntity> addresses) {
        return CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Doe")
            .addresses(addresses)
            .build();
    }

    private static CasePartyEntity party(String firstName, String lastName) {
        return CasePartyEntity.builder()
            .firstName(firstName)
            .lastName(lastName)
            .build();
    }

    private static AddressEntity address() {
        return AddressEntity.builder()
            .addressLine1("10 Downing Street")
            .addressLine2("Westminster")
            .postTown("London")
            .county("Greater London")
            .postcode("SW1A 2AA")
            .build();
    }

    private static void assertLandlord(
        PTCase result,
        LandlordRepresentativeType representativeType
    ) {
        assertThat(result.getLandlordDetails()).isNotNull();
        assertThat(result.getLandlordDetails().getRepresentativeType())
            .isEqualTo(representativeType);
        assertThat(result.getLandlordDetails().getLandlordPartyDetails())
            .satisfies(party -> {
                assertThat(party.getFirstName()).isEqualTo("John");
                assertThat(party.getLastName()).isEqualTo("Smith");
            });
    }
}
