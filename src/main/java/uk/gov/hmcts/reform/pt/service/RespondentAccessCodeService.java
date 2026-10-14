package uk.gov.hmcts.reform.pt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;
import uk.gov.hmcts.reform.pt.util.AccessCodeGenerator;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RespondentAccessCodeService {

    private final AccessCodeGenerator generator;
    private final PasswordEncoder encoder;
    private final PartyAccessCodeRepository repository;
    private final Clock clock;
    private final Duration validity;

    public RespondentAccessCodeService(AccessCodeGenerator generator,
                                       PasswordEncoder encoder,
                                       PartyAccessCodeRepository repository,
                                       Clock clock,
                                       @Value("${access-code.validity}") Duration validity) {
        this.generator = generator;
        this.encoder = encoder;
        this.repository = repository;
        this.clock = clock;
        this.validity = validity;
    }

    @Transactional
    public String createFor(PTCaseEntity ptCase, CasePartyEntity respondent) {
        if (!ptCase.getId().equals(respondent.getPtCase().getId())) {
            throw new IllegalArgumentException("Party does not belong to case " + ptCase.getId());
        }

        String code = generator.generate();
        LocalDateTime now = LocalDateTime.now(clock);

        PartyAccessCodeEntity entity = repository
            .findByPtCaseIdAndPartyId(ptCase.getId(), respondent.getId())
            .orElseGet(() -> PartyAccessCodeEntity.builder().ptCase(ptCase).party(respondent).build());

        entity.setCode(encoder.encode(code));
        entity.setExpiresAt(now.plus(validity));
        repository.save(entity);
        return code;
    }
}
