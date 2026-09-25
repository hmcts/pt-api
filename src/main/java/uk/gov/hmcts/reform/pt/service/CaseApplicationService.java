package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.ApplicationMapper;
import uk.gov.hmcts.reform.pt.mapper.ApplicationSummaryMapper;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.util.CaseReferenceUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseApplicationService {

    private final CaseApplicationRepository applicationRepository;
    private final ApplicationSummaryMapper applicationSummaryMapper;

    public List<ApplicationDto> getCasesForUser(UUID userId) {
        return applicationSummaryMapper.toDtos(
            applicationRepository.findActiveByCasePartyAccessIdamId(userId)
        );
    }

    public EnrichedApplicationDto getCaseByCaseReference(long caseReference, UUID userId) {
        if (!CaseReferenceUtils.isValidCaseReference(caseReference)) {
            throw new InvalidCaseReferenceException(caseReference);
        }

        return applicationRepository.findByPartyIdamIdAndCaseReference(caseReference, userId)
            .map(ApplicationMapper::toDto)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));
    }
}
