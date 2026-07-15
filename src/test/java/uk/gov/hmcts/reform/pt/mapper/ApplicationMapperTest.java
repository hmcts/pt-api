package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.entity.CaseApplication;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.CaseProperty;
import uk.gov.hmcts.reform.pt.entity.CaseType;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplicationMapperTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String POSTCODE = "AB12 3CD";
    private static final String APPLICATION_TYPE = "CHALLENGE_RENT_INCREASE";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "test@test.com";

    @Test
    public void shouldMapToDtoFromEntity() {
        UUID userId = UUID.randomUUID();
        CaseApplication entity = fullEntity(userId);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        assertThat(result.getApplicationType()).isEqualTo(APPLICATION_TYPE);
        assertThat(result.getApplicantFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getApplicantLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getApplicantIdamUserId()).isEqualTo(userId);
    }

    @Test
    public void shouldThrowCasePartyNotFoundExceptionWhenCasePartyIsNull() {
        CaseApplication entity = CaseApplication.builder()
            .caseParty(null)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CasePartyNotFoundException.class)
            .hasMessageContaining("Case party not found for application");
    }

    @Test
    public void shouldThrowCaseNotFoundExceptionWhenPtCaseIsNull() {
        CaseParty caseParty = caseParty(null, Collections.emptyList());
        CaseApplication entity = CaseApplication.builder()
            .caseParty(caseParty)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessageContaining("Case not found for application");
    }

    @Test
    public void shouldDefaultPostcodeToEmptyStringWhenNoProperties() {
        PTCaseEntity ptCase = ptCase(Collections.emptyList());
        CaseParty caseParty = caseParty(ptCase, Collections.emptyList());
        CaseApplication entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getPostcode()).isEqualTo("");
    }

    @Test
    public void shouldDefaultApplicationTypeToEmptyStringWhenCaseTypeIsNull() {
        CaseParty caseParty = caseParty(ptCase(properties(POSTCODE)), Collections.emptyList());
        CaseApplication entity = entityWithCaseType(caseParty, null);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicationType()).isEqualTo("");
    }

    @Test
    public void shouldDefaultApplicantIdamUserIdToNullWhenNoAccessRecords() {
        CaseParty caseParty = caseParty(ptCase(properties(POSTCODE)), Collections.emptyList());
        CaseApplication entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicantIdamUserId()).isNull();
    }

    private static List<CaseProperty> properties(String postcode) {
        return List.of(CaseProperty.builder().postcode(postcode).build());
    }

    private static PTCaseEntity ptCase(List<CaseProperty> properties) {
        return PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .properties(properties)
            .build();
    }

    private static CaseParty caseParty(PTCaseEntity ptCase, List<CasePartyAccess> access) {
        return CaseParty.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .emailAddress(EMAIL)
            .ptCase(ptCase)
            .access(access)
            .build();
    }

    private static CaseApplication entityWithCaseType(CaseParty caseParty, String typeName) {
        return CaseApplication.builder()
            .caseParty(caseParty)
            .caseType(typeName != null ? CaseType.builder().applicationTypeName(typeName).build() : null)
            .build();
    }

    private static CaseApplication fullEntity(UUID userId) {
        PTCaseEntity ptCase = ptCase(properties(POSTCODE));
        List<CasePartyAccess> access = List.of(
            CasePartyAccess.builder().idamId(userId).build()
        );
        CaseParty caseParty = caseParty(ptCase, access);
        return entityWithCaseType(caseParty, APPLICATION_TYPE);
    }
}
