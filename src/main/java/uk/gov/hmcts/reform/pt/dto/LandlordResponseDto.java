package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

import java.util.List;

@Getter
@Builder
public class LandlordResponseDto {
    private YesOrNo respondToTenantAnswersAboutTenancyDetails;
    private String respondToTenantAnswersAboutTenancyDetailsDescription;
    private YesOrNo respondToTenantAnswersAboutPropertyDetails;
    private String respondToTenantAnswersAboutPropertyDetailsDescription;
    private String landlordPhoneNumber;
    private List<DocumentDto> tenancyDetailsResponseEvidence;
}
