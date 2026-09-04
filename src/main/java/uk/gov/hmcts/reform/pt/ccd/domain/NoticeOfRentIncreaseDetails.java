package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class NoticeOfRentIncreaseDetails {
    @CCD(
        label = "Has received landlord notice proposing new rent",
        access = {CitizenAccess.class}
    )
    private YesOrNo receivedLandlordNoticeProposingNewRent;

    @CCD(
        label = "Reason for no upload of notice proposing new rent",
        access = {CitizenAccess.class}
    )
    private String noUploadOfNoticeProposingNewRentReason;

    @CCD(
        label = "Landlord notice proposing new rent",
        access = {CitizenAccess.class}
    )
    @JsonDeserialize(using = UploadedDocumentDeserializer.class)
    private UploadedDocument landlordNoticeProposingNewRentDocument;

    @CCD(
        label = "Is notice legally valid",
        access = {CitizenAccess.class}
    )
    private YesOrNo noticeLegallyValid;


    @CCD(
        label = "Details why notice is not legally valid",
        access = {CitizenAccess.class}
    )
    private String noticeNotLegallyValidDetails;

    @CCD(
        label = "Notice for not legally valid document",
        access = {CitizenAccess.class}
    )
    @JsonDeserialize(using = UploadedDocumentDeserializer.class)
    private UploadedDocument noticeNotLegallyValidDocument;

    @CCD(
        label = "Will rent increase cause hardship",
        access = {CitizenAccess.class}
    )
    private YesOrNo rentIncreaseToCauseHardship;

    @CCD(
        label = "Rent increase to cause hardship document",
        access = {CitizenAccess.class}
    )
    @JsonDeserialize(using = UploadedDocumentDeserializer.class)
    private UploadedDocument rentIncreaseToCauseHardshipDocument;
}
