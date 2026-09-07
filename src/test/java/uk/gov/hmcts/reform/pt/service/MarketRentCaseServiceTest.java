package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.CurrentRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.MarketRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.MarketRentCaseRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarketRentCaseServiceTest {

    @Mock
    private MarketRentCaseRepository marketRentCaseRepository;

    @InjectMocks
    private MarketRentCaseService marketRentCaseService;

    @Test
    @DisplayName("Should update existing MarketRentCaseEntity with property details")
    void updateWithPropertyDetailsWhenExists() {
        MarketRentCaseEntity existing = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(List.of(existing))
            .build();

        PropertyDetails details = PropertyDetails.builder()
            .propertyType(PropertyType.FLAT)
            .rentingFlatDetails("Top floor flat")
            .rentingRoomDetails("Room 1")
            .otherMethodRentingDetails("Other method")
            .propertyFloorPlanAvailable(YesOrNo.YES)
            .floorPlanManualDetails("Manual plan")
            .indoorFeatures("Central heating")
            .sharePropertyWithLandlord(YesOrNo.NO)
            .sharePropertyWithLandlordDetails("No sharing")
            .build();

        marketRentCaseService.updateWithPropertyDetails(ptCase, details);

        verify(marketRentCaseRepository).save(existing);
        assertThat(existing.getPtCase()).isEqualTo(ptCase);
        assertThat(existing.getTypeOfPropertyRenting()).isEqualTo(PropertyType.FLAT);
        assertThat(existing.getRentingFlatDetails()).isEqualTo("Top floor flat");
        assertThat(existing.getRentingRoomDetails()).isEqualTo("Room 1");
        assertThat(existing.getOtherMethodOfRentDetails()).isEqualTo("Other method");
        assertThat(existing.getPropertyFloorPlanAvailable()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getFloorplanManualDetails()).isEqualTo("Manual plan");
        assertThat(existing.getPropertyIndoorFeatures()).isEqualTo("Central heating");
        assertThat(existing.getSharePropertyWithLandlord()).isEqualTo(YesOrNo.NO);
        assertThat(existing.getSharePropertyWithLandlordDetails()).isEqualTo("No sharing");
    }

    @Test
    @DisplayName("Should create and save new MarketRentCaseEntity with property details when list is empty")
    void updateWithPropertyDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(new ArrayList<>())
            .build();

        PropertyDetails details = PropertyDetails.builder()
            .propertyType(PropertyType.TERRACED_HOUSE)
            .propertyFloorPlanAvailable(YesOrNo.NO)
            .build();

        marketRentCaseService.updateWithPropertyDetails(ptCase, details);

        ArgumentCaptor<MarketRentCaseEntity> captor = ArgumentCaptor.forClass(MarketRentCaseEntity.class);
        verify(marketRentCaseRepository).save(captor.capture());
        MarketRentCaseEntity saved = captor.getValue();

        assertThat(saved.getPtCase()).isEqualTo(ptCase);
        assertThat(saved.getTypeOfPropertyRenting()).isEqualTo(PropertyType.TERRACED_HOUSE);
        assertThat(saved.getPropertyFloorPlanAvailable()).isEqualTo(YesOrNo.NO);
    }

    @Test
    @DisplayName("Should update existing MarketRentCaseEntity with current rent details")
    void updateWithCurrentRentDetailsWhenExists() {
        MarketRentCaseEntity existing = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(List.of(existing))
            .build();

        CurrentRentDetails details = CurrentRentDetails.builder()
            .rentPaymentFrequency(Frequency.MONTHLY)
            .rentCostWeekly(150d)
            .rentCostFortnightly(300d)
            .rentCostMonthly(600d)
            .rentCostYearly(7200d)
            .rentIncludesCouncilTax(YesOrNo.YES)
            .councilTaxFrequency(Frequency.MONTHLY)
            .councilTaxCostWeekly(25d)
            .councilTaxCostFortnightly(50d)
            .councilTaxCostMonthly(100d)
            .councilTaxCostYearly(1200d)
            .councilTaxFrequencyAndCostDetails("Council tax breakdown")
            .utilitiesPaidFrequency(Frequency.MONTHLY)
            .utilitiesPaidCostWeekly(20d)
            .utilitiesPaidCostFortnightly(40d)
            .utilitiesPaidCostMonthly(80d)
            .utilitiesPaidCostYearly(960d)
            .utilitiesPaidFrequencyAndCostDetails("Utilities breakdown")
            .anyOtherHouseholdManagementCharges(YesOrNo.YES)
            .otherHouseholdManagementChargesDetails("Other charges")
            .build();

        marketRentCaseService.updateWithCurrentRentDetails(ptCase, details);

        verify(marketRentCaseRepository).save(existing);
        assertThat(existing.getRentPaymentFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getRentCostWeekly()).isEqualTo(BigDecimal.valueOf(150d));
        assertThat(existing.getRentCostFortnightly()).isEqualTo(BigDecimal.valueOf(300d));
        assertThat(existing.getRentCostMonthly()).isEqualTo(BigDecimal.valueOf(600d));
        assertThat(existing.getRentCostYearly()).isEqualTo(BigDecimal.valueOf(7200d));
        assertThat(existing.getRentIncludesCouncilTax()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getCouncilTaxFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getCouncilTaxCostWeekly()).isEqualTo(BigDecimal.valueOf(25d));
        assertThat(existing.getCouncilTaxCostFortnightly()).isEqualTo(BigDecimal.valueOf(50d));
        assertThat(existing.getCouncilTaxCostMonthly()).isEqualTo(BigDecimal.valueOf(100d));
        assertThat(existing.getCouncilTaxCostYearly()).isEqualTo(BigDecimal.valueOf(1200d));
        assertThat(existing.getCouncilTaxFrequencyAndCostDetails()).isEqualTo("Council tax breakdown");
        assertThat(existing.getUtilitiesPaidFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getUtilitiesCostWeekly()).isEqualTo(BigDecimal.valueOf(20d));
        assertThat(existing.getUtilitiesCostFortnightly()).isEqualTo(BigDecimal.valueOf(40d));
        assertThat(existing.getUtilitiesCostMonthly()).isEqualTo(BigDecimal.valueOf(80d));
        assertThat(existing.getUtilitiesCostYearly()).isEqualTo(BigDecimal.valueOf(960d));
        assertThat(existing.getUtilitiesFrequencyAndCostDetails()).isEqualTo("Utilities breakdown");
        assertThat(existing.getOtherHouseholdManagementCharges()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getOtherHouseholdManagementChargesDetails()).isEqualTo("Other charges");
    }

    @Test
    @DisplayName("Should create and save new MarketRentCaseEntity with current rent details when list is empty")
    void updateWithCurrentRentDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(new ArrayList<>())
            .build();

        CurrentRentDetails details = CurrentRentDetails.builder()
            .rentPaymentFrequency(Frequency.WEEKLY)
            .rentCostWeekly(200d)
            .build();

        marketRentCaseService.updateWithCurrentRentDetails(ptCase, details);

        ArgumentCaptor<MarketRentCaseEntity> captor = ArgumentCaptor.forClass(MarketRentCaseEntity.class);
        verify(marketRentCaseRepository).save(captor.capture());
        MarketRentCaseEntity saved = captor.getValue();

        assertThat(saved.getRentPaymentFrequency()).isEqualTo(Frequency.WEEKLY);
        assertThat(saved.getRentCostWeekly()).isEqualTo(BigDecimal.valueOf(200d));
    }

    @Test
    @DisplayName("Should update existing MarketRentCaseEntity with market rent details")
    void updateWithMarketRentDetailsWhenExists() {
        MarketRentCaseEntity existing = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(List.of(existing))
            .build();

        MarketRentDetails details = MarketRentDetails.builder()
            .applicantSuggestedMonthlyMarketRent(1200d)
            .applicantSuggestedMonthlyMarketRentReasons("Market rate for area")
            .additionalInfoToConsiderWhenDeterminingRent(YesOrNo.YES)
            .additionalInfoToConsiderWhenDeterminingRentDetails("Renovated")
            .build();

        marketRentCaseService.updateWithMarketRentDetails(ptCase, details);

        verify(marketRentCaseRepository).save(existing);
        assertThat(existing.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(BigDecimal.valueOf(1200d));
        assertThat(existing.getApplicantSuggestedMonthlyMarketRentReasons()).isEqualTo("Market rate for area");
        assertThat(existing.getAdditionalPropertyInfoToConsiderWhenDeterminingRent()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails())
            .isEqualTo("Renovated");
    }

    @Test
    @DisplayName("Should create and save new MarketRentCaseEntity with market rent details when list is empty")
    void updateWithMarketRentDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(new ArrayList<>())
            .build();

        MarketRentDetails details = MarketRentDetails.builder()
            .applicantSuggestedMonthlyMarketRent(950d)
            .applicantSuggestedMonthlyMarketRentReasons("Reason for rate")
            .additionalInfoToConsiderWhenDeterminingRent(YesOrNo.NO)
            .build();

        marketRentCaseService.updateWithMarketRentDetails(ptCase, details);

        ArgumentCaptor<MarketRentCaseEntity> captor = ArgumentCaptor.forClass(MarketRentCaseEntity.class);
        verify(marketRentCaseRepository).save(captor.capture());
        MarketRentCaseEntity saved = captor.getValue();

        assertThat(saved.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(BigDecimal.valueOf(950d));
        assertThat(saved.getApplicantSuggestedMonthlyMarketRentReasons()).isEqualTo("Reason for rate");
        assertThat(saved.getAdditionalPropertyInfoToConsiderWhenDeterminingRent()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails()).isNull();
    }
}
