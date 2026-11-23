package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CaseFormatterTest {

    @Test
    void shouldFormatCaseNameWithIndividualLandlordName() {
        CasePartyEntity applicant = CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Doe")
            .build();

        CasePartyEntity respondent = CasePartyEntity.builder()
            .firstName("John")
            .lastName("Smith")
            .build();

        String caseName = CaseFormatter.formatCaseName(applicant, respondent);

        assertThat(caseName).isEqualTo("Jane Doe vs John Smith");
    }

    @Test
    void shouldFormatCaseNameWithLandlordOrganisationNameWhenFirstAndLastNameAreBlank() {
        CasePartyEntity applicant = CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Doe")
            .build();

        CasePartyEntity respondent = CasePartyEntity.builder()
            .firstName("")
            .lastName("")
            .organisationName("Acme Estates Ltd")
            .build();

        String caseName = CaseFormatter.formatCaseName(applicant, respondent);

        assertThat(caseName).isEqualTo("Jane Doe vs Acme Estates Ltd");
    }

    @Test
    void shouldReturnEmptyStringWhenApplicantOrRespondentIsNull() {
        CasePartyEntity applicant = CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Doe")
            .build();

        CasePartyEntity respondent = CasePartyEntity.builder()
            .firstName("John")
            .lastName("Smith")
            .build();

        assertThat(CaseFormatter.formatCaseName(null, respondent)).isEmpty();
        assertThat(CaseFormatter.formatCaseName(applicant, null)).isEmpty();
        assertThat(CaseFormatter.formatCaseName(null, null)).isEmpty();
    }
}
