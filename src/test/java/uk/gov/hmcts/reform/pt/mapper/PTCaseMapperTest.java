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
            .applicantIdamUserId(UUID.randomUUID())
            .caseReference(1234567890123456L)
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .applicantFirstName("FirstName")
            .applicantLastName("LastName")
            .email("test@test.com")
            .postcode("AB12 3CD")
            .build();

        CaseDto result = PTCaseMapper.toDto(entity);

        assertThat(result).usingRecursiveComparison().isEqualTo(entity);
    }
}
