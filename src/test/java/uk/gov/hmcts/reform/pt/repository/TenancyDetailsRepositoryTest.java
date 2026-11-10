package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TenancyDetailsRepositoryTest extends AbstractRepositoryTest<TenancyDetailsRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseRepository marketRentCaseRepository;

    @Autowired
    protected TenancyDetailsRepositoryTest(
        TenancyDetailsRepository repository,
        PTCaseRepository ptCaseRepository,
        MarketRentCaseRepository marketRentCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.marketRentCaseRepository = marketRentCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve tenancy details entity")
    void saveAndFindTenancyDetails() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        MarketRentCaseEntity marketRentCase = marketRentCaseRepository.save(
            MarketRentCaseEntity.builder().ptCase(ptCase).build());

        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tenancyType(TenancyType.ASSURED_PERIODIC_TENANCY)
            .documentUploaded(YesOrNo.YES)
            .additionalServicesProvidedInTenancy(YesOrNo.NO)
            .marketRentCase(marketRentCase)
            .build();

        TenancyDetailsEntity saved = repository.save(tenancyDetails);

        Optional<TenancyDetailsEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getTenancyType()).isEqualTo(TenancyType.ASSURED_PERIODIC_TENANCY);
        assertThat(result.get().getDocumentUploaded()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getAdditionalServicesProvidedInTenancy()).isEqualTo(YesOrNo.NO);
        assertThat(result.get().getMarketRentCase().getId()).isEqualTo(marketRentCase.getId());

        Optional<TenancyDetailsEntity> byTypeAndRef = repository
            .findFirstByTenancyTypeAndMarketRentCase_PtCase_CaseReference(
                TenancyType.ASSURED_PERIODIC_TENANCY, caseReference);
        assertThat(byTypeAndRef).isPresent();
        assertThat(byTypeAndRef.get().getId()).isEqualTo(saved.getId());
    }
}
