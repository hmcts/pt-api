package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.ApplicationMapper;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.util.CaseReferenceUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CLOSED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.DRAFT_DISCARDED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PENDING_CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.REQUESTED_FOR_DELETION;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PendingDisposal;

@Service
@RequiredArgsConstructor
public class CaseApplicationService {

    public static final List<String> APPLICANT_EXCLUDED_STATES = Stream.of(
            PendingDisposal,
            REQUESTED_FOR_DELETION,
            CLOSED
        )
        .map(State::name)
        .toList();

    public static final List<String> RESPONDENT_EXCLUDED_STATES = Stream.of(
            PendingDisposal,
            AWAITING_SUBMISSION_TO_HMCTS,
            PENDING_CASE_ISSUED,
            DRAFT_DISCARDED,
            CASE_ISSUED,
            REQUESTED_FOR_DELETION,
            CLOSED
        )
        .map(State::name)
        .toList();

    private final CasePartyRepository casePartyRepository;

    public List<ApplicationDto> getCasesForUser(UUID userId) {
        return casePartyRepository.findActiveApplicationsByIdamId(userId, APPLICANT_EXCLUDED_STATES)
            .stream()
            .map(ApplicationMapper::toDto)
            .toList();
    }

    public List<ApplicationDto> getRespondentCasesForUser(UUID respondentUserId) {
        return casePartyRepository.findRespondentApplicationsByIdamId(respondentUserId, RESPONDENT_EXCLUDED_STATES)
            .stream()
            .map(ApplicationMapper::toDto)
            .toList();
    }

    public EnrichedApplicationDto getCaseByCaseReference(long caseReference, UUID userId) {
        if (!CaseReferenceUtils.isValidCaseReference(caseReference)) {
            throw new InvalidCaseReferenceException(caseReference);
        }

        return casePartyRepository.findByIdamIdAndCaseReference(caseReference, userId)
            .map(ApplicationMapper::toEnrichedDto)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));
    }
}
