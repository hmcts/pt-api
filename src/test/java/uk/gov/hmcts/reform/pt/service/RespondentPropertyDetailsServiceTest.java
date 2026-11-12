package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordResponse;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.RespondentPropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.RespondentPropertyDetailsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RespondentPropertyDetailsServiceTest {

    @Mock
    private RespondentPropertyDetailsRepository respondentPropertyDetailsRepository;

    @Mock
    private MarketRentCaseService marketRentCaseService;

    @InjectMocks
    private RespondentPropertyDetailsService respondentPropertyDetailsService;

    @Test
    @DisplayName("Should create new respondent property details entity when none exists")
    void updateLandlordResponseCreatesNew() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCaseEntity)).thenReturn(marketRentCase);

        LandlordResponse landlordResponse = LandlordResponse.builder()
            .respondToTenantAnswersAboutTenancyDetails(YesOrNo.YES)
            .respondToTenantAnswersAboutTenancyDetailsDescription("Tenancy started in March, not January")
            .build();

        respondentPropertyDetailsService.updateLandlordResponse(landlordResponse, ptCaseEntity);

        ArgumentCaptor<RespondentPropertyDetailsEntity> captor =
            ArgumentCaptor.forClass(RespondentPropertyDetailsEntity.class);
        verify(respondentPropertyDetailsRepository).save(captor.capture());
        RespondentPropertyDetailsEntity saved = captor.getValue();

        assertThat(saved.getMarketRentCase()).isEqualTo(marketRentCase);
        assertThat(marketRentCase.getRespondentPropertyDetails()).isEqualTo(saved);
        assertThat(saved.getRespondToTenantAnswersAboutTenancyDetails()).isEqualTo(YesOrNo.YES);
        assertThat(saved.getRespondToTenantAnswersAboutTenancyDetailsDescription())
            .isEqualTo("Tenancy started in March, not January");
    }

    @Test
    @DisplayName("Should update existing respondent property details entity when one exists")
    void updateLandlordResponseUpdatesExisting() {
        RespondentPropertyDetailsEntity existing = RespondentPropertyDetailsEntity.builder()
            .respondToTenantAnswersAboutTenancyDetails(YesOrNo.NO)
            .respondToTenantAnswersAboutTenancyDetailsDescription("Old description")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .respondentPropertyDetails(existing)
            .build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCaseEntity)).thenReturn(marketRentCase);

        LandlordResponse landlordResponse = LandlordResponse.builder()
            .respondToTenantAnswersAboutTenancyDetails(YesOrNo.YES)
            .respondToTenantAnswersAboutTenancyDetailsDescription("Updated description")
            .build();

        respondentPropertyDetailsService.updateLandlordResponse(landlordResponse, ptCaseEntity);

        verify(respondentPropertyDetailsRepository).save(existing);
        assertThat(existing.getMarketRentCase()).isEqualTo(marketRentCase);
        assertThat(existing.getRespondToTenantAnswersAboutTenancyDetails()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getRespondToTenantAnswersAboutTenancyDetailsDescription())
            .isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should leave existing values untouched when the response fields are null")
    void updateLandlordResponseKeepsExistingValuesWhenNull() {
        RespondentPropertyDetailsEntity existing = RespondentPropertyDetailsEntity.builder()
            .respondToTenantAnswersAboutTenancyDetails(YesOrNo.YES)
            .respondToTenantAnswersAboutTenancyDetailsDescription("Existing description")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .respondentPropertyDetails(existing)
            .build();
        when(marketRentCaseService.getOrCreateMarketRentCase(ptCaseEntity)).thenReturn(marketRentCase);

        respondentPropertyDetailsService.updateLandlordResponse(LandlordResponse.builder().build(), ptCaseEntity);

        verify(respondentPropertyDetailsRepository).save(existing);
        assertThat(existing.getRespondToTenantAnswersAboutTenancyDetails()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getRespondToTenantAnswersAboutTenancyDetailsDescription())
            .isEqualTo("Existing description");
    }
}
