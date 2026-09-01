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
            .rentCostWeekly(new BigDecimal("150.00"))
            .rentCostFortnightly(new BigDecimal("300.00"))
            .rentCostMonthly(new BigDecimal("600.00"))
            .rentCostYearly(new BigDecimal("7200.00"))
            .rentIncludesCouncilTax(YesOrNo.YES)
            .councilTaxFrequency(Frequency.MONTHLY)
            .councilTaxCostWeekly(new BigDecimal("25.00"))
            .councilTaxCostFortnightly(new BigDecimal("50.00"))
            .councilTaxCostMonthly(new BigDecimal("100.00"))
            .councilTaxCostYearly(new BigDecimal("1200.00"))
            .councilTaxFrequencyAndCostDetails("Council tax breakdown")
            .utilitiesPaidFrequency(Frequency.MONTHLY)
            .utilitiesPaidCostWeekly(new BigDecimal("20.00"))
            .utilitiesPaidCostFortnightly(new BigDecimal("40.00"))
            .utilitiesPaidCostMonthly(new BigDecimal("80.00"))
            .utilitiesPaidCostYearly(new BigDecimal("960.00"))
            .utilitiesPaidFrequencyAndCostDetails("Utilities breakdown")
            .build();

        marketRentCaseService.updateWithCurrentRentDetails(ptCase, details);

        verify(marketRentCaseRepository).save(existing);
        assertThat(existing.getRentPaymentFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getRentCostWeekly()).isEqualTo(new BigDecimal("150.00"));
        assertThat(existing.getRentCostFortnightly()).isEqualTo(new BigDecimal("300.00"));
        assertThat(existing.getRentCostMonthly()).isEqualTo(new BigDecimal("600.00"));
        assertThat(existing.getRentCostYearly()).isEqualTo(new BigDecimal("7200.00"));
        assertThat(existing.getRentIncludesCouncilTax()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getCouncilTaxFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getCouncilTaxCostWeekly()).isEqualTo(new BigDecimal("25.00"));
        assertThat(existing.getCouncilTaxCostFortnightly()).isEqualTo(new BigDecimal("50.00"));
        assertThat(existing.getCouncilTaxCostMonthly()).isEqualTo(new BigDecimal("100.00"));
        assertThat(existing.getCouncilTaxCostYearly()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(existing.getCouncilTaxFrequencyAndCostDetails()).isEqualTo("Council tax breakdown");
        assertThat(existing.getUtilitiesPaidFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(existing.getUtilitiesCostWeekly()).isEqualTo(new BigDecimal("20.00"));
        assertThat(existing.getUtilitiesCostFortnightly()).isEqualTo(new BigDecimal("40.00"));
        assertThat(existing.getUtilitiesCostMonthly()).isEqualTo(new BigDecimal("80.00"));
        assertThat(existing.getUtilitiesCostYearly()).isEqualTo(new BigDecimal("960.00"));
        assertThat(existing.getUtilitiesFrequencyAndCostDetails()).isEqualTo("Utilities breakdown");
    }

    @Test
    @DisplayName("Should create and save new MarketRentCaseEntity with current rent details when list is empty")
    void updateWithCurrentRentDetailsWhenEmpty() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(new ArrayList<>())
            .build();

        CurrentRentDetails details = CurrentRentDetails.builder()
            .rentPaymentFrequency(Frequency.WEEKLY)
            .rentCostWeekly(new BigDecimal("200.00"))
            .build();

        marketRentCaseService.updateWithCurrentRentDetails(ptCase, details);

        ArgumentCaptor<MarketRentCaseEntity> captor = ArgumentCaptor.forClass(MarketRentCaseEntity.class);
        verify(marketRentCaseRepository).save(captor.capture());
        MarketRentCaseEntity saved = captor.getValue();

        assertThat(saved.getRentPaymentFrequency()).isEqualTo(Frequency.WEEKLY);
        assertThat(saved.getRentCostWeekly()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Should update existing MarketRentCaseEntity with market rent details")
    void updateWithMarketRentDetailsWhenExists() {
        MarketRentCaseEntity existing = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .marketRentCases(List.of(existing))
            .build();

        MarketRentDetails details = MarketRentDetails.builder()
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1200.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Market rate for area")
            .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.YES)
            .additionalPropertyInfoToConsiderWhenDeterminingDetails("Renovated")
            .build();

        marketRentCaseService.updateWithMarketRentDetails(ptCase, details);

        verify(marketRentCaseRepository).save(existing);
        assertThat(existing.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(new BigDecimal("1200.00"));
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
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("950.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Reason for rate")
            .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.NO)
            .build();

        marketRentCaseService.updateWithMarketRentDetails(ptCase, details);

        ArgumentCaptor<MarketRentCaseEntity> captor = ArgumentCaptor.forClass(MarketRentCaseEntity.class);
        verify(marketRentCaseRepository).save(captor.capture());
        MarketRentCaseEntity saved = captor.getValue();

        assertThat(saved.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(new BigDecimal("950.00"));
        assertThat(saved.getApplicantSuggestedMonthlyMarketRentReasons()).isEqualTo("Reason for rate");
        assertThat(saved.getAdditionalPropertyInfoToConsiderWhenDeterminingRent()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails()).isNull();
    }
}
