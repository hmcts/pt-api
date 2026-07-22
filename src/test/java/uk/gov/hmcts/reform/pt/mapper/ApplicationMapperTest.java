package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
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
    private static final ApplicationType APPLICATION_TYPE = ApplicationType.CHALLENGE_RENT_INCREASE;
    private static final String POSTCODE = "AB12 3CD";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "test@test.com";

    @Test
    public void shouldMapToDtoFromEntity() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = fullEntity(userId);

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
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(null)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CasePartyNotFoundException.class)
            .hasMessageContaining("Case party not found for application");
    }

    @Test
    public void shouldThrowCaseNotFoundExceptionWhenPtCaseIsNull() {
        CasePartyEntity caseParty = caseParty(null, Collections.emptyList());
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessageContaining("Case not found for application");
    }

    @Test
    public void shouldDefaultPostcodeToEmptyStringWhenNoProperties() {
        PTCaseEntity ptCase = ptCase(Collections.emptyList());
        CasePartyEntity caseParty = caseParty(ptCase, Collections.emptyList());
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getPostcode()).isEqualTo("");
    }

    @Test
    public void shouldDefaultApplicationTypeToNullWhenCaseTypeIsNull() {
        CasePartyEntity caseParty = caseParty(ptCase(addresses(POSTCODE)), Collections.emptyList());
        CaseApplicationEntity entity = entityWithCaseType(caseParty, null);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicationType()).isNull();
    }

    @Test
    public void shouldDefaultApplicantIdamUserIdToNullWhenNoAccessRecords() {
        CasePartyEntity caseParty = caseParty(ptCase(addresses(POSTCODE)), Collections.emptyList());
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicantIdamUserId()).isNull();
    }

    private static List<AddressEntity> addresses(String postcode) {
        return List.of(AddressEntity.builder().postcode(postcode).build());
    }

    private static PTCaseEntity ptCase(List<AddressEntity> addresses) {
        return PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .addresses(addresses)
            .build();
    }

    private static CasePartyEntity caseParty(PTCaseEntity ptCase, List<CasePartyAccessEntity> access) {
        return CasePartyEntity.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .emailAddress(EMAIL)
            .ptCase(ptCase)
            .access(access)
            .build();
    }

    private static CaseApplicationEntity entityWithCaseType(
        CasePartyEntity caseParty,
        ApplicationType applicationType
    ) {
        return CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(
                applicationType != null
                    ? CaseTypeEntity.builder().applicationTypeName(applicationType).build()
                    : null)
            .build();
    }

    private static CaseApplicationEntity fullEntity(UUID userId) {
        PTCaseEntity ptCase = ptCase(addresses(POSTCODE));
        List<CasePartyAccessEntity> access = List.of(
            CasePartyAccessEntity.builder().idamId(userId).build()
        );
        CasePartyEntity caseParty = caseParty(ptCase, access);
        return entityWithCaseType(caseParty, APPLICATION_TYPE);
    }
}
