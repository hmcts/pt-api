package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;
import uk.gov.hmcts.reform.pt.util.AccessCodeGenerator;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RespondentAccessCodeService {

    private final AccessCodeGenerator generator;
    private final PasswordEncoder encoder;
    private final PartyAccessCodeRepository repository;
    private final Clock clock;
    @Value("${access-code.validity}")
    private final Duration validity;

    @Transactional
    public String createFor(PTCaseEntity ptCase, CasePartyEntity respondent) {
        if (!ptCase.getId().equals(respondent.getPtCase().getId())) {
            throw new IllegalArgumentException("Party does not belong to case " + ptCase.getId());
        }

        PartyAccessCodeEntity entity = repository
            .findByPtCaseIdAndPartyId(ptCase.getId(), respondent.getId())
            .orElseGet(() -> PartyAccessCodeEntity.builder()
                .ptCase(ptCase)
                .party(respondent)
                .build());

        if (respondent.getIdamId() != null || entity.getUsedAt() != null) {
            throw new AccessCodeAlreadyUsedException("This access code has already been used");
        }

        String code = generator.generate();
        entity.setCode(encoder.encode(code));
        entity.setExpiresAt(LocalDateTime.now(clock).plus(validity));
        repository.save(entity);
        return code;
    }
}
