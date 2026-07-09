package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.PTCaseMapper;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.util.CaseReferenceUtils;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final PTCaseRepository ptCaseRepository;

    public void createCase(
        long caseReference,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .applicantFirstName(ptCase.getApplicantForename())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }

    @Transactional
    public List<CaseDto> getCasesForUser(UUID userId) {
        return ptCaseRepository.findAllByIdamUserId(userId).stream()
            .map(PTCaseMapper::toDto)
            .toList();
    }

    @Transactional
    public CaseDto getCaseByCaseReference(long caseReference, UUID userId) {
        if (!CaseReferenceUtils.isValidCaseReference(caseReference)) {
            throw new InvalidCaseReferenceException(caseReference);
        }

        return ptCaseRepository.findByCaseReferenceAndIdamUserId(caseReference, userId)
            .map(PTCaseMapper::toDto)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));
    }
}
