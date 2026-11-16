package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordResponse;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.RespondentPropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.RespondentPropertyDetailsRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class RespondentPropertyDetailsService {
    private final RespondentPropertyDetailsRepository respondentPropertyDetailsRepository;
    private final MarketRentCaseService marketRentCaseService;

    @Transactional
    public void updateLandlordResponse(LandlordResponse landlordResponse, PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = marketRentCaseService.getOrCreateMarketRentCase(ptCaseEntity);
        RespondentPropertyDetailsEntity entity = marketRentCase.getRespondentPropertyDetails();
        if (entity == null) {
            entity = new RespondentPropertyDetailsEntity();
            marketRentCase.setRespondentPropertyDetails(entity);
        }

        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutTenancyDetails(),
            entity::setRespondToTenantAnswersAboutTenancyDetails
        );
        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutTenancyDetailsDescription(),
            entity::setRespondToTenantAnswersAboutTenancyDetailsDescription
        );
        entity.setMarketRentCase(marketRentCase);

        respondentPropertyDetailsRepository.save(entity);
    }
}
