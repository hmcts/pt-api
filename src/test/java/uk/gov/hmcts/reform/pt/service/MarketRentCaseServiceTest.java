package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.MarketRentCaseRepository;

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
}
