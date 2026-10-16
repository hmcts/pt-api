package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.NoticeOfRentChangeRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class NoticeOfRentChangeService {
    private final NoticeOfRentChangeRepository noticeOfRentChangeRepository;
    private final MarketRentCaseService marketRentCaseService;

    @Transactional
    public void updateNoticeOfRentChangeDetails(NoticeOfRentIncreaseDetails details, PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = marketRentCaseService.getOrCreateMarketRentCase(ptCaseEntity);
        NoticeOfRentChangeEntity entity = marketRentCase.getNoticeOfRentChange();
        if (entity == null) {
            entity = new NoticeOfRentChangeEntity();
            marketRentCase.setNoticeOfRentChange(entity);
        }

        setIfNotNull(
            details.getReceivedLandlordNoticeProposingNewRent(),
            entity::setReceivedLandlordNoticeProposingNewRent
        );
        setIfNotNull(
            details.getNoUploadOfNoticeProposingNewRentReason(),
            entity::setNoUploadOfNoticeProposingNewRentReason
        );
        setIfNotNull(details.getNoticeLegallyValid(), entity::setNoticeLegallyValid);
        setIfNotNull(details.getNoticeNotLegallyValidDetails(), entity::setNoticeNotLegallyValidDetails);
        setIfNotNull(details.getRentIncreaseToCauseHardship(), entity::setRentIncreaseToCauseHardship);
        entity.setMarketRentCase(marketRentCase);

        noticeOfRentChangeRepository.save(entity);
    }
}
