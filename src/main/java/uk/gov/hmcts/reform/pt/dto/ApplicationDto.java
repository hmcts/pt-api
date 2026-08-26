package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApplicationDto {
    private long caseReference;
    private String applicantFirstName;
    private String applicantLastName;
    private String email;
    private String postcode;
    private UUID applicantIdamUserId;
    private ApplicationType applicationType;
    private TenancyType tenancyType;
    private LocalDateTime createdDate;
    private ContactPreferencesDto applicantContactPreferences;
    private TenantDetailsDto tenantDetails;
    private HearingInspectionDetailsDto hearingInspectionDetails;
    private NoticeOfRentIncreaseDto noticeOfRentIncreaseDetails;
    private PropertyDetailsDto propertyDetails;
    private CurrentRentsDetailsDto currentRentsDetails;
    private MarketRentDto marketRentDetails;
}
