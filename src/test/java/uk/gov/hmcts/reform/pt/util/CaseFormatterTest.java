package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;

import static org.assertj.core.api.Assertions.assertThat;

class CaseFormatterTest {

    @Test
    void shouldFormatCaseNameWithIndividualLandlordName() {
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("Jane")
            .applicantLastName("Doe")
            .landlordDetails(LandlordDetails.builder()
                .landlordPartyDetails(PartyDetails.builder()
                    .firstName("John")
                    .lastName("Smith")
                    .build())
                .build())
            .build();

        String caseName = CaseFormatter.formatCaseName(ptCase);

        assertThat(caseName).isEqualTo("Jane Doe vs John Smith");
    }

    @Test
    void shouldFormatCaseNameWithLandlordOrganisationNameWhenFirstAndLastNameAreBlank() {
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("Jane")
            .applicantLastName("Doe")
            .landlordDetails(LandlordDetails.builder()
                .landlordPartyDetails(PartyDetails.builder()
                    .firstName("")
                    .lastName("")
                    .organisationName("Acme Estates Ltd")
                    .build())
                .build())
            .build();

        String caseName = CaseFormatter.formatCaseName(ptCase);

        assertThat(caseName).isEqualTo("Jane Doe vs Acme Estates Ltd");
    }
}
