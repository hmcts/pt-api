package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationFeeRepositoryTest extends AbstractRepositoryTest<ApplicationFeeRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseRepository marketRentCaseRepository;

    @Autowired
    protected ApplicationFeeRepositoryTest(
        ApplicationFeeRepository repository,
        PTCaseRepository ptCaseRepository,
        MarketRentCaseRepository marketRentCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.marketRentCaseRepository = marketRentCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve application fee entity")
    void saveAndFindApplicationFee() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        MarketRentCaseEntity marketRentCase = marketRentCaseRepository.save(
            MarketRentCaseEntity.builder().ptCase(ptCase).build());

        LocalDateTime requestDate = LocalDateTime.of(2026, 11, 5, 10, 30, 0);
        BigDecimal amount = new BigDecimal("100.00");

        ApplicationFeeEntity fee = ApplicationFeeEntity.builder()
            .requestReference("RC-1700-0000-0000-0001")
            .requestDate(requestDate)
            .amount(amount)
            .status("Success")
            .externalReference("EXT-98765")
            .marketRentCase(marketRentCase)
            .build();

        ApplicationFeeEntity saved = repository.save(fee);

        Optional<ApplicationFeeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getRequestReference()).isEqualTo("RC-1700-0000-0000-0001");
        assertThat(result.get().getRequestDate()).isEqualTo(requestDate);
        assertThat(result.get().getAmount()).isEqualByComparingTo(amount);
        assertThat(result.get().getStatus()).isEqualTo("Success");
        assertThat(result.get().getExternalReference()).isEqualTo("EXT-98765");
        assertThat(result.get().getMarketRentCase().getId()).isEqualTo(marketRentCase.getId());
        assertThat(result.get().getMarketRentCase().getPtCase().getCaseReference()).isEqualTo(caseReference);
        assertThat(result.get().getCreatedDate()).isNotNull();
        assertThat(result.get().getLastModifiedDate()).isNotNull();
    }
}
