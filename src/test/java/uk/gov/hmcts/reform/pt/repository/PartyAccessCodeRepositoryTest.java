package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PartyAccessCodeRepositoryTest extends AbstractRepositoryTest<PartyAccessCodeRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;

    @Autowired
    protected PartyAccessCodeRepositoryTest(
        PartyAccessCodeRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
    }

    @Test
    @DisplayName("Should save and retrieve party access code entity")
    void saveAndFindPartyAccessCode() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        CasePartyEntity party = casePartyRepository.save(
            CasePartyEntity.builder()
                .ptCase(ptCase)
                .firstName("John")
                .lastName("Smith")
                .build());

        LocalDateTime expiresAt = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        PartyAccessCodeEntity accessCode = PartyAccessCodeEntity.builder()
            .ptCase(ptCase)
            .party(party)
            .code("ABC123XYZ")
            .expiresAt(expiresAt)
            .build();

        PartyAccessCodeEntity saved = repository.save(accessCode);

        Optional<PartyAccessCodeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getCode()).isEqualTo("ABC123XYZ");
        assertThat(result.get().getExpiresAt()).isEqualTo(expiresAt);
        assertThat(result.get().getPtCase().getId()).isEqualTo(ptCase.getId());
        assertThat(result.get().getParty().getId()).isEqualTo(party.getId());

        Optional<PartyAccessCodeEntity> byCaseAndParty = repository.findByPtCaseIdAndPartyId(
            ptCase.getId(), party.getId());
        assertThat(byCaseAndParty).isPresent();
        assertThat(byCaseAndParty.get().getId()).isEqualTo(saved.getId());
    }
}
