package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenancyDetailsServiceTest {

    @Mock
    private TenancyDetailsRepository tenancyDetailsRepository;

    @Mock
    private MarketRentCaseService marketRentCaseService;

    @InjectMocks
    private TenancyDetailsService tenancyDetailsService;

    @Test
    @DisplayName("Should return existing TenancyDetailsEntity if it exists")
    void getTenancyDetailsOrCreateIfNotExistsWhenExists() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;
        TenancyDetailsEntity existingTenancyDetails = TenancyDetailsEntity.builder().tenancyType(tenancyType).build();

        when(tenancyDetailsRepository.findFirstByTenancyTypeAndMarketRentCase_PtCase_CaseReference(
            tenancyType, caseReference))
            .thenReturn(Optional.of(existingTenancyDetails));

        TenancyDetailsEntity result =
            tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(tenancyType, ptCase);

        assertThat(result).isEqualTo(existingTenancyDetails);
        verify(tenancyDetailsRepository).findFirstByTenancyTypeAndMarketRentCase_PtCase_CaseReference(
            tenancyType, caseReference);
        verify(tenancyDetailsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new TenancyDetailsEntity if it does not exist")
    void getTenancyDetailsOrCreateIfNotExistsWhenNotExists() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;

        when(tenancyDetailsRepository.findFirstByTenancyTypeAndMarketRentCase_PtCase_CaseReference(
            tenancyType, caseReference))
            .thenReturn(Optional.empty());
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);
        when(tenancyDetailsRepository.save(any(TenancyDetailsEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        TenancyDetailsEntity result =
            tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(tenancyType, ptCase);

        assertThat(result.getTenancyType()).isEqualTo(tenancyType);
        verify(tenancyDetailsRepository).findFirstByTenancyTypeAndMarketRentCase_PtCase_CaseReference(
            tenancyType, caseReference);
        verify(tenancyDetailsRepository).save(any(TenancyDetailsEntity.class));
    }

    @Test
    @DisplayName("Should create TenancyDetailsEntity")
    void createTenancyDetails() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);
        when(tenancyDetailsRepository.save(any(TenancyDetailsEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        TenancyDetailsEntity result = tenancyDetailsService.createTenancyDetails(tenancyType, ptCase);

        assertThat(result.getTenancyType()).isEqualTo(tenancyType);
        assertThat(result.getMarketRentCase()).isEqualTo(marketRentCase);
        assertThat(marketRentCase.getTenancyDetails()).isEqualTo(result);
        verify(tenancyDetailsRepository).save(any(TenancyDetailsEntity.class));
    }

    @Test
    @DisplayName("Should update existing TenancyDetailsEntity with property details")
    void updateWithPropertyDetailsWhenExists() {
        TenancyDetailsEntity existing = TenancyDetailsEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .tenancyDetails(existing)
            .build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        PropertyDetails details = PropertyDetails.builder()
            .otherFacilitiesAvailable(YesOrNo.YES)
            .otherFacilitiesDetails("Parking")
            .furnitureProvidedInTenancy(YesOrNo.YES)
            .furnitureProvidedInTenancyDetails("Bed, Wardrobe")
            .additionalServicesProvidedInTenancy(YesOrNo.NO)
            .additionalServicesProvidedInTenancyDetails("None")
            .landlordRepairsDetails("Boiler repair")
            .tenantRepairsDetails("Fixed shelf")
            .anyTenantsMadePropertyRepairs(YesNoNotSure.YES)
            .build();

        tenancyDetailsService.updateWithPropertyDetails(ptCase, details);

        verify(tenancyDetailsRepository).save(existing);
        assertThat(existing.getTenancyIncludeFacilities()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getOtherFacilitiesDetails()).isEqualTo("Parking");
        assertThat(existing.getFurnitureProvidedInTenancy()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getFurnitureProvidedInTenancyDetails()).isEqualTo("Bed, Wardrobe");
        assertThat(existing.getAdditionalServicesProvidedInTenancy()).isEqualTo(YesOrNo.NO);
        assertThat(existing.getAdditionalServicesProvidedInTenancyDetails()).isEqualTo("None");
        assertThat(existing.getLandlordRepairsDetails()).isEqualTo("Boiler repair");
        assertThat(existing.getTenantRepairsDetails()).isEqualTo("Fixed shelf");
        assertThat(existing.getAnyTenantsMadePropertyRepairs()).isEqualTo(YesNoNotSure.YES);
    }

    @Test
    @DisplayName("Should create and save TenancyDetailsEntity with property details when list is empty")
    void updateWithPropertyDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        PropertyDetails details = PropertyDetails.builder()
            .otherFacilitiesAvailable(YesOrNo.NO)
            .landlordRepairsDetails("Roof leak")
            .build();

        tenancyDetailsService.updateWithPropertyDetails(ptCase, details);

        ArgumentCaptor<TenancyDetailsEntity> captor = ArgumentCaptor.forClass(TenancyDetailsEntity.class);
        verify(tenancyDetailsRepository).save(captor.capture());
        TenancyDetailsEntity saved = captor.getValue();

        assertThat(saved.getMarketRentCase()).isEqualTo(marketRentCase);
        assertThat(marketRentCase.getTenancyDetails()).isEqualTo(saved);
        assertThat(saved.getTenancyIncludeFacilities()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getLandlordRepairsDetails()).isEqualTo("Roof leak");
    }

    @Test
    @DisplayName("Should update existing TenancyDetailsEntity with rent details")
    void updateWithRentDetailsWhenExists() {
        TenancyDetailsEntity existing = TenancyDetailsEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .tenancyDetails(existing)
            .build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 1);
        LocalDate originalStartDate = LocalDate.of(2024, 1, 1);

        RentDetails details = RentDetails.builder()
            .tribunalPreviouslyDeterminedTenancyRent(YesOrNo.YES)
            .previousTribunalCaseReference("TRIB-123")
            .currentTenancyStartDate(startDate)
            .currentTenancyEndDate(endDate)
            .currentTenancyReplaceOriginalTenancy(YesNoNotSure.YES)
            .originalTenancyStartDate(originalStartDate)
            .build();

        tenancyDetailsService.updateWithRentDetails(ptCase, details);

        verify(tenancyDetailsRepository).save(existing);
        assertThat(existing.getTribunalPreviouslyDeterminedTenancyRent()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getPreviousTribunalCaseReference()).isEqualTo("TRIB-123");
        assertThat(existing.getCurrentTenancyStartDate()).isEqualTo(startDate.atStartOfDay());
        assertThat(existing.getTenancyEndDate()).isEqualTo(endDate.atStartOfDay());
        assertThat(existing.getCurrentTenancyReplaceOriginalTenancy()).isEqualTo(YesNoNotSure.YES);
        assertThat(existing.getOriginalTenancyStartDate()).isEqualTo(originalStartDate.atStartOfDay());
    }

    @Test
    @DisplayName("Should create and save TenancyDetailsEntity with rent details when list is empty")
    void updateWithRentDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        RentDetails details = RentDetails.builder()
            .tribunalPreviouslyDeterminedTenancyRent(YesOrNo.NO)
            .currentTenancyStartDate(startDate)
            .build();

        tenancyDetailsService.updateWithRentDetails(ptCase, details);

        ArgumentCaptor<TenancyDetailsEntity> captor = ArgumentCaptor.forClass(TenancyDetailsEntity.class);
        verify(tenancyDetailsRepository).save(captor.capture());
        TenancyDetailsEntity saved = captor.getValue();

        assertThat(saved.getTribunalPreviouslyDeterminedTenancyRent()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getCurrentTenancyStartDate()).isEqualTo(startDate.atStartOfDay());
    }

    @Test
    @DisplayName("Should update existing TenancyDetailsEntity with tenancy agreement details")
    void updateWithTenancyAgreementDetailsWhenExists() {
        TenancyDetailsEntity existing = TenancyDetailsEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .tenancyDetails(existing)
            .build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        TenancyAgreementDetails details = TenancyAgreementDetails.builder()
            .copyOfTenancyAgreement(YesOrNo.YES)
            .noTenancyAgreementReason("Reason")
            .build();

        tenancyDetailsService.updateWithTenancyAgreementDetails(ptCase, details);

        verify(tenancyDetailsRepository).save(existing);
        assertThat(existing.getCopyOfTenancyAgreement()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getNoTenancyAgreementReason()).isEqualTo("Reason");
    }

    @Test
    @DisplayName("Should create and save TenancyDetailsEntity with tenancy agreement details when list is empty")
    void updateWithTenancyAgreementDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCase)).thenReturn(marketRentCase);

        TenancyAgreementDetails details = TenancyAgreementDetails.builder()
            .copyOfTenancyAgreement(YesOrNo.NO)
            .noTenancyAgreementReason("No agreement available")
            .build();

        tenancyDetailsService.updateWithTenancyAgreementDetails(ptCase, details);

        ArgumentCaptor<TenancyDetailsEntity> captor = ArgumentCaptor.forClass(TenancyDetailsEntity.class);
        verify(tenancyDetailsRepository).save(captor.capture());
        TenancyDetailsEntity saved = captor.getValue();

        assertThat(saved.getCopyOfTenancyAgreement()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getNoTenancyAgreementReason()).isEqualTo("No agreement available");
    }
}
