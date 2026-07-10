package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

public class PTCaseMapper {
    public static CaseDto toDto(PTCaseEntity entity) {
        return CaseDto.builder()
            .caseReference(entity.getCaseReference())
            .applicantFirstName(entity.getApplicantFirstName())
            .applicantLastName(entity.getApplicantLastName())
            .email(entity.getEmail())
            .postcode(entity.getPostcode())
            .applicantIdamUserId(entity.getApplicantIdamUserId())
            .applicationType(entity.getApplicationType())
            .build();
    }
}
