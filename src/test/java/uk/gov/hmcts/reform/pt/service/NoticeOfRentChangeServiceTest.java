package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.NoticeOfRentChangeRepository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeOfRentChangeServiceTest {

    @Mock
    private NoticeOfRentChangeRepository noticeOfRentChangeRepository;

    @Mock
    private CasePropertyDetailsService casePropertyDetailsService;

    @InjectMocks
    private NoticeOfRentChangeService noticeOfRentChangeService;

    @Test
    @DisplayName("Should create new notice of rent change entity when none exists")
    void updateNoticeOfRentChangeDetailsCreatesNew() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePropertyDetailsEntity casePropertyDetails = CasePropertyDetailsEntity.builder().build();
        when(casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCaseEntity)).thenReturn(casePropertyDetails);

        NoticeOfRentIncreaseDetails details = NoticeOfRentIncreaseDetails.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("Lost paper copy")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidDetails("Invalid date on notice")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .rentIncreaseToCauseHardshipDetails("Financial hardship")
            .build();

        noticeOfRentChangeService.updateNoticeOfRentChangeDetails(details, ptCaseEntity);

        ArgumentCaptor<NoticeOfRentChangeEntity> captor =
            ArgumentCaptor.forClass(NoticeOfRentChangeEntity.class);
        verify(noticeOfRentChangeRepository).save(captor.capture());
        NoticeOfRentChangeEntity saved = captor.getValue();

        assertThat(saved.getCasePropertyDetails()).isEqualTo(casePropertyDetails);
        assertThat(casePropertyDetails.getNoticeOfRentChange()).isEqualTo(saved);
        assertThat(saved.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(saved.getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("Lost paper copy");
        assertThat(saved.getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(saved.getNoticeNotLegallyValidReason()).isEqualTo("Invalid date on notice");
        assertThat(saved.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);
        assertThat(saved.getRentIncreaseToCauseHardshipDetails()).isEqualTo("Financial hardship");
    }

    @Test
    @DisplayName("Should update existing notice of rent change entity when one exists")
    void updateNoticeOfRentChangeDetailsUpdatesExisting() {
        NoticeOfRentChangeEntity existing = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.NO)
            .noUploadOfNoticeProposingNewRentReason("Old reason")
            .noticeLegallyValid(YesOrNo.YES)
            .noticeNotLegallyValidReason(null)
            .rentIncreaseToCauseHardship(YesOrNo.NO)
            .rentIncreaseToCauseHardshipDetails("Old hardship")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePropertyDetailsEntity casePropertyDetails = CasePropertyDetailsEntity.builder()
            .noticeOfRentChange(existing)
            .build();
        when(casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCaseEntity)).thenReturn(casePropertyDetails);

        NoticeOfRentIncreaseDetails details = NoticeOfRentIncreaseDetails.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("New reason")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidDetails("Updated details")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .rentIncreaseToCauseHardshipDetails("Updated hardship")
            .build();

        noticeOfRentChangeService.updateNoticeOfRentChangeDetails(details, ptCaseEntity);

        verify(noticeOfRentChangeRepository).save(existing);
        assertThat(existing.getCasePropertyDetails()).isEqualTo(casePropertyDetails);
        assertThat(existing.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("New reason");
        assertThat(existing.getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(existing.getNoticeNotLegallyValidReason()).isEqualTo("Updated details");
        assertThat(existing.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getRentIncreaseToCauseHardshipDetails()).isEqualTo("Updated hardship");
    }
}
