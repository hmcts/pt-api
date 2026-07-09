package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

public class PTCaseMapper {
    public static CaseDto toDto(PTCaseEntity entity) {
        return CaseDto.builder()
            .id(entity.getId())
            .caseReference(entity.getCaseReference())
            .applicantFirstName(entity.getApplicantFirstName())
            .applicantLastName(entity.getApplicantLastName())
            .email(entity.getEmail())
            .postcode(entity.getPostcode())
            .idamUserId(entity.getIdamUserId())
            .applicationType(entity.getApplicationType())
            .build();
    }
}
