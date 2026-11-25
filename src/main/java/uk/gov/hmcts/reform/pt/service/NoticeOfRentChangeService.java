package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.NoticeOfRentChangeRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class NoticeOfRentChangeService {
    private final NoticeOfRentChangeRepository noticeOfRentChangeRepository;
    private final CasePropertyDetailsService casePropertyDetailsService;

    @Transactional
    public void updateNoticeOfRentChangeDetails(NoticeOfRentIncreaseDetails details, PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails =
            casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCaseEntity);
        NoticeOfRentChangeEntity entity = casePropertyDetails.getNoticeOfRentChange();
        if (entity == null) {
            entity = new NoticeOfRentChangeEntity();
            casePropertyDetails.setNoticeOfRentChange(entity);
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
        setIfNotNull(details.getRentIncreaseToCauseHardshipDetails(), entity::setRentIncreaseToCauseHardshipDetails);
        entity.setCasePropertyDetails(casePropertyDetails);

        noticeOfRentChangeRepository.save(entity);
    }
}
