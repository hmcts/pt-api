package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.entity.FeeHelpWithFeesEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FeeHelpWithFeesRepositoryTest extends AbstractRepositoryTest<FeeHelpWithFeesRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseRepository marketRentCaseRepository;
    private final ApplicationFeeRepository applicationFeeRepository;

    @Autowired
    protected FeeHelpWithFeesRepositoryTest(
        FeeHelpWithFeesRepository repository,
        PTCaseRepository ptCaseRepository,
        MarketRentCaseRepository marketRentCaseRepository,
        ApplicationFeeRepository applicationFeeRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.marketRentCaseRepository = marketRentCaseRepository;
        this.applicationFeeRepository = applicationFeeRepository;
    }

    @Test
    @DisplayName("Should save and retrieve fee help with fees entity")
    void saveAndFindFeeHelpWithFees() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        MarketRentCaseEntity marketRentCase = marketRentCaseRepository.save(
            MarketRentCaseEntity.builder().ptCase(ptCase).build());
        ApplicationFeeEntity fee = applicationFeeRepository.save(
            ApplicationFeeEntity.builder()
                .requestReference("RC-1700-0000-0000-0001")
                .requestDate(LocalDateTime.now())
                .amount(new BigDecimal("100.00"))
                .status("Success")
                .marketRentCase(marketRentCase)
                .build()
        );

        FeeHelpWithFeesEntity helpWithFees = FeeHelpWithFeesEntity.builder()
            .referenceNumber(123456)
            .applicationFee(fee)
            .build();

        FeeHelpWithFeesEntity saved = repository.save(helpWithFees);

        Optional<FeeHelpWithFeesEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getReferenceNumber()).isEqualTo(123456);
        assertThat(result.get().getApplicationFee().getId()).isEqualTo(fee.getId());
    }
}
