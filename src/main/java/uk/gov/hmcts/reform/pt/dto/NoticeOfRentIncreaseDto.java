package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Data
@Builder
public class NoticeOfRentIncreaseDto {
    private YesOrNo receivedLandlordNoticeProposingNewRent;
    private String noUploadOfNoticeProposingNewRentReason;
    private DocumentDto landlordNoticeProposingNewRentDocument;

    private YesOrNo noticeLegallyValid;
    private String noticeNotLegallyValidDetails;
    private DocumentDto noticeNotLegallyValidDocument;

    private YesOrNo rentIncreaseToCauseHardship;
    private DocumentDto rentIncreaseToCauseHardshipDocument;
}
