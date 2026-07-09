package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PTCaseMapperTest {

    @Test
    public void shouldMapToDtoFromEntity() {
        PTCaseEntity entity = PTCaseEntity.builder()
            .id(UUID.randomUUID())
            .idamUserId(UUID.randomUUID())
            .caseReference(1234567890123456L)
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .applicantFirstName("FirstName")
            .applicantLastName("LastName")
            .email("test@test.com")
            .postcode("AB12 3CD")
            .build();

        CaseDto result = PTCaseMapper.toDto(entity);

        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getIdamUserId()).isEqualTo(entity.getIdamUserId());
        assertThat(result.getCaseReference()).isEqualTo(entity.getCaseReference());
        assertThat(result.getApplicationType()).isEqualTo(entity.getApplicationType());
        assertThat(result.getApplicantFirstName()).isEqualTo(entity.getApplicantFirstName());
        assertThat(result.getApplicantLastName()).isEqualTo(entity.getApplicantLastName());
        assertThat(result.getEmail()).isEqualTo(entity.getEmail());
        assertThat(result.getPostcode()).isEqualTo(entity.getPostcode());
    }
}
