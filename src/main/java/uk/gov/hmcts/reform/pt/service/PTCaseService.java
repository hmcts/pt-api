package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.ccd.api.CcdApiClient;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.PTCaseMapper;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final PTCaseRepository ptCaseRepository;
    private final CcdApiClient ccdApi;

    public void createCase(
        long caseReference,
        UUID userId,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .applicantIdamUserId(userId)
            .applicantFirstName(ptCase.getApplicantFirstName())
            .applicantLastName(ptCase.getApplicantLastName())
            .email(ptCase.getEmail())
            .postcode(ptCase.getPostcode())
            .applicationType(ptCase.getApplicationType())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }
}
