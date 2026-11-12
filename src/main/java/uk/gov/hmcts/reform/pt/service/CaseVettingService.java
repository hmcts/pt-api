package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationVettingEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.MissingInformationRequestEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationVettingRepository;
import uk.gov.hmcts.reform.pt.repository.MissingInformationRequestRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class CaseVettingService {
    private final CaseApplicationVettingRepository caseApplicationVettingRepository;
    private final MissingInformationRequestRepository missingInformationRequestRepository;
    private final PTCaseService ptCaseService;

    @Transactional
    public void updateCaseVetting(long caseReference, PTCase ptCase) {
        PTCaseEntity ptCaseEntity = ptCaseService.getCase(caseReference);
        CaseApplicationVettingEntity vettingEntity = findOrBuildCaseVettingEntity(ptCaseEntity);
        caseApplicationVettingRepository.save(vettingEntity);

        updateMissingInfoRequest(vettingEntity, ptCase);
    }

    @Transactional
    public void updateMissingInfoRequest(CaseApplicationVettingEntity vettingEntity, PTCase ptCase) {
        MissingInformationRequestEntity missingInfoRequest = findOrBuildMissingInfoRequestEntity(vettingEntity);
        if (ptCase.getVettingInfoRequested().isEmpty()) {
            return;
        }
        missingInfoRequest.setMissingDocumentTypes(ptCase.getVettingInfoRequested());
        setIfNotNull(ptCase.getVettingInfoOther(), missingInfoRequest::setOtherDocumentsInformationDescription);

        missingInformationRequestRepository.save(missingInfoRequest);
    }

    private CaseApplicationVettingEntity findOrBuildCaseVettingEntity(PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCase();
        if (marketRentCase == null) {
            throw new IllegalStateException(
                "Market rent case not found for case reference: " + ptCaseEntity.getCaseReference()
            );
        }

        CaseApplicationVettingEntity vettingEntity = marketRentCase.getCaseApplicationVetting();

        if (vettingEntity == null) {
            vettingEntity = new CaseApplicationVettingEntity();
            marketRentCase.setCaseApplicationVetting(vettingEntity);
        }
        vettingEntity.setMarketRentCase(marketRentCase);
        return caseApplicationVettingRepository.save(vettingEntity);
    }

    private MissingInformationRequestEntity findOrBuildMissingInfoRequestEntity(
        CaseApplicationVettingEntity vettingEntity
    ) {
        MissingInformationRequestEntity missingInfo = vettingEntity.getMissingInformationRequest();
        if (missingInfo == null) {
            missingInfo = new MissingInformationRequestEntity();
            vettingEntity.setMissingInformationRequest(missingInfo);
        }
        missingInfo.setCaseApplicationVetting(vettingEntity);
        return missingInfo;
    }
}
