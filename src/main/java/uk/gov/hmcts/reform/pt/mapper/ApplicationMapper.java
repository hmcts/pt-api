package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

public class ApplicationMapper {
    public static ApplicationDto toDto(CaseApplicationEntity entity) {
        CasePartyEntity caseParty = entity.getCaseParty();
        if (caseParty == null) {
            throw new CasePartyNotFoundException("Case party not found for application: " + entity.getId());
        }
        PTCaseEntity ptCase = caseParty.getPtCase();
        if (ptCase == null) {
            throw new CaseNotFoundException("Case not found for application: " + entity.getId());
        }

        return ApplicationDto.builder()
            .caseReference(ptCase.getCaseReference())
            .postcode(
                !ptCase.getAddresses().isEmpty()
                    ? ptCase.getAddresses().getFirst().getPostcode()
                    : "")
            .applicationType(
                entity.getCaseType() != null
                    ? entity.getCaseType().getApplicationTypeName()
                    : null)
            .applicantFirstName(caseParty.getFirstName())
            .applicantLastName(caseParty.getLastName())
            .applicantIdamUserId(
                !caseParty.getAccess().isEmpty()
                    ? caseParty.getAccess().getFirst().getIdamId()
                    : null)
            .email(caseParty.getEmailAddress())
            .build();
    }
}
