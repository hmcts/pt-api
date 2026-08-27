package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.NoticeOfRentChangeRepository;

@Service
@RequiredArgsConstructor
public class NoticeOfRentChangeService {
    private final NoticeOfRentChangeRepository noticeOfRentChangeRepository;

    @Transactional
    public void updateNoticeOfRentChangeDetails(NoticeOfRentIncreaseDetails details, PTCaseEntity ptCaseEntity) {
        NoticeOfRentChangeEntity entity = ptCaseEntity.getNoticeOfRentChanges().stream()
            .findFirst()
            .orElse(new NoticeOfRentChangeEntity());

        entity.setReceivedLandlordNoticeProposingNewRent(details.getReceivedLandlordNoticeProposingNewRent());
        entity.setNoUploadOfNoticeProposingNewRentReason(details.getNoUploadOfNoticeProposingNewRentReason());
        entity.setNoticeLegallyValid(details.getNoticeLegallyValid());
        entity.setNoticeNotLegallyValidDetails(details.getNoticeNotLegallyValidDetails());
        entity.setRentIncreaseToCauseHardship(details.getRentIncreaseToCauseHardship());
        entity.setPtCase(ptCaseEntity);

        noticeOfRentChangeRepository.save(entity);
    }
}
