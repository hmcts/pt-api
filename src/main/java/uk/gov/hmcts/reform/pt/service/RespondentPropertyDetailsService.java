package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordResponse;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.RespondentPropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.RespondentPropertyDetailsRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class RespondentPropertyDetailsService {
    private final RespondentPropertyDetailsRepository respondentPropertyDetailsRepository;
    private final CasePropertyDetailsService casePropertyDetailsService;

    @Transactional
    public void updateLandlordResponse(LandlordResponse landlordResponse, PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails =
            casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCaseEntity);
        RespondentPropertyDetailsEntity entity = casePropertyDetails.getRespondentPropertyDetails();
        if (entity == null) {
            entity = new RespondentPropertyDetailsEntity();
            casePropertyDetails.setRespondentPropertyDetails(entity);
        }

        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutTenancyDetails(),
            entity::setRespondToTenantAnswersAboutTenancyDetails
        );
        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutTenancyDetailsDescription(),
            entity::setRespondToTenantAnswersAboutTenancyDetailsDescription
        );
        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutPropertyDetails(),
            entity::setRespondToTenantAnswersAboutPropertyDetails
        );
        setIfNotNull(
            landlordResponse.getRespondToTenantAnswersAboutPropertyDetailsDescription(),
            entity::setRespondToTenantAnswersAboutPropertyDetailsDescription
        );
        entity.setCasePropertyDetails(casePropertyDetails);

        respondentPropertyDetailsRepository.save(entity);
    }
}
