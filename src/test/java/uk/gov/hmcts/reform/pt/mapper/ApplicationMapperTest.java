package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplicationMapperTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final ApplicationType APPLICATION_TYPE = ApplicationType.CHALLENGE_RENT_INCREASE;
    private static final TenancyType TENANCY_TYPE = TenancyType.ASSURED_PERIODIC_TENANCY;
    private static final String POSTCODE = "AB12 3CD";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "test@test.com";
    private static final String PHONE_NUMBER = "0123456789";
    private static final String MOBILE_NUMBER = "0712345678";
    private static final String COMPANY_NAME = "Test Company Ltd";
    private static final String REFERENCE_NUMBER = "REF12";
    private static final LocalDateTime CREATED_DATE = LocalDateTime.of(2026, 2, 24, 9, 0);

    @Test
    public void shouldMapToDtoFromEntity() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = fullEntity(userId);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        assertThat(result.getApplicationType()).isEqualTo(APPLICATION_TYPE);
        assertThat(result.getTenancyType()).isEqualTo(TENANCY_TYPE);
        assertThat(result.getApplicantFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getApplicantLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getApplicantIdamUserId()).isEqualTo(userId);
        assertThat(result.getCreatedDate()).isEqualTo(CREATED_DATE);

        ContactPreferencesDto contactPreferences = result.getApplicantContactPreferences();
        assertThat(contactPreferences.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(contactPreferences.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(contactPreferences.getContactByText()).isEqualTo(YesOrNo.YES);

        TenantDetailsDto tenantDetails = result.getTenantDetails();
        assertThat(tenantDetails.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(tenantDetails.getLastName()).isEqualTo(LAST_NAME);
        assertThat(tenantDetails.getCompanyName()).isEqualTo(COMPANY_NAME);
        assertThat(tenantDetails.getReferenceNumberForCommunications()).isEqualTo(REFERENCE_NUMBER);

        HearingInspectionDetailsDto hearingInspectionDetails = result.getHearingInspectionDetails();
        assertThat(hearingInspectionDetails.getHearingRequested()).isEqualTo(YesOrNo.YES);
        assertThat(hearingInspectionDetails.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(hearingInspectionDetails.getNoDecisionWithoutInspectionReason()).isEqualTo("Inspection reason");

        NoticeOfRentIncreaseDto noticeOfRentIncreaseDetails = result.getNoticeOfRentIncreaseDetails();
        assertThat(noticeOfRentIncreaseDetails).isNotNull();
    }

    @Test
    public void shouldThrowCasePartyNotFoundExceptionWhenCasePartyIsNull() {
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(null)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CasePartyNotFoundException.class)
            .hasMessageContaining("Case party not found for application");
    }

    @Test
    public void shouldThrowCaseNotFoundExceptionWhenPtCaseIsNull() {
        CasePartyEntity caseParty = caseParty(null, Collections.emptyList(), Collections.emptyList());
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessageContaining("Case not found for application");
    }

    @Test
    public void shouldMapContactPreferences() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            contactPreferences(YesOrNo.YES, YesOrNo.NO)
        );

        ContactPreferencesDto result = ApplicationMapper.mapContactPreferences(caseParty);

        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(result.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(result.getContactByText()).isEqualTo(YesOrNo.YES);
    }

    @Test
    public void shouldMapContactPreferencesWhenNoPreferences() {
        CasePartyEntity caseParty = caseParty(null, Collections.emptyList(), Collections.emptyList());

        ContactPreferencesDto result = ApplicationMapper.mapContactPreferences(caseParty);

        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(result.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(result.getContactByText()).isNull();
    }

    @Test
    public void shouldMapTenantDetails() {
        CasePartyEntity caseParty = caseParty(null, Collections.emptyList(), Collections.emptyList());

        TenantDetailsDto result = ApplicationMapper.mapTenantDetails(caseParty);

        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getCompanyName()).isEqualTo(COMPANY_NAME);
        assertThat(result.getReferenceNumberForCommunications()).isEqualTo(REFERENCE_NUMBER);
    }

    @Test
    public void shouldMapTenantDetailsWhenFieldsNull() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();

        TenantDetailsDto result = ApplicationMapper.mapTenantDetails(caseParty);

        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getCompanyName()).isNull();
        assertThat(result.getReferenceNumberForCommunications()).isNull();
    }

    @Test
    public void shouldMapHearingInspectionDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .hearingRequested(YesOrNo.NO)
            .propertyInspections(List.of(
                PropertyInspectionEntity.builder()
                    .agreeToDecisionWithoutInspection(YesOrNo.YES)
                    .noDecisionWithoutInspectionReason("Some reason")
                    .build()
            ))
            .build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isEqualTo(YesOrNo.NO);
        assertThat(result.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoDecisionWithoutInspectionReason()).isEqualTo("Some reason");
    }

    @Test
    public void shouldMapHearingInspectionDetailsWhenNoPropertyInspections() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .hearingRequested(YesOrNo.YES)
            .propertyInspections(Collections.emptyList())
            .build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isEqualTo(YesOrNo.YES);
        assertThat(result.getAgreeToDecisionWithoutInspection()).isNull();
        assertThat(result.getNoDecisionWithoutInspectionReason()).isNull();
    }

    @Test
    public void shouldMapHearingInspectionDetailsWhenFieldsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isNull();
        assertThat(result.getAgreeToDecisionWithoutInspection()).isNull();
        assertThat(result.getNoDecisionWithoutInspectionReason()).isNull();
    }

    @Test
    public void shouldDefaultPostcodeToEmptyStringWhenNoProperties() {
        PTCaseEntity ptCase = ptCase(Collections.emptyList(), Collections.emptyList());
        CasePartyEntity caseParty = caseParty(ptCase, Collections.emptyList(), Collections.emptyList());
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getPostcode()).isEqualTo("");
    }

    @Test
    public void shouldDefaultApplicationTypeToNullWhenCaseTypeIsNull() {
        CasePartyEntity caseParty = caseParty(
            ptCase(addresses(POSTCODE), Collections.emptyList()),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, null);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicationType()).isNull();
    }

    @Test
    public void shouldDefaultApplicantIdamUserIdToNullWhenNoAccessRecords() {
        CasePartyEntity caseParty = caseParty(
            ptCase(addresses(POSTCODE), Collections.emptyList()),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicantIdamUserId()).isNull();
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetails() {
        DocumentEntity rentNoticeDoc = DocumentEntity.builder()
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .url("http://dm-store/doc/1")
            .binaryUrl("http://dm-store/doc/1/binary")
            .fileName("rent_notice.pdf")
            .contentType("application/pdf")
            .size(1024L)
            .build();

        DocumentEntity invalidNoticeDoc = DocumentEntity.builder()
            .documentType(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE)
            .url("http://dm-store/doc/2")
            .binaryUrl("http://dm-store/doc/2/binary")
            .fileName("invalid_notice.pdf")
            .contentType("application/pdf")
            .size(2048L)
            .build();

        DocumentEntity hardshipDoc = DocumentEntity.builder()
            .documentType(DocumentType.HARDSHIP_EVIDENCE)
            .url("http://dm-store/doc/3")
            .binaryUrl("http://dm-store/doc/3/binary")
            .fileName("hardship.pdf")
            .contentType("application/pdf")
            .size(4096L)
            .build();

        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("Paper copy only")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidDetails("Incorrect notice period")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(List.of(noticeOfRentChange))
            .documents(List.of(rentNoticeDoc, invalidNoticeDoc, hardshipDoc))
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("Paper copy only");
        assertThat(result.getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(result.getNoticeNotLegallyValidDetails()).isEqualTo("Incorrect notice period");
        assertThat(result.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);

        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/1")
                .binaryUrl("http://dm-store/doc/1/binary")
                .filename("rent_notice.pdf")
                .contentType("application/pdf")
                .size(1024L)
                .build()
        );
        assertThat(result.getNoticeNotLegallyValidDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/2")
                .binaryUrl("http://dm-store/doc/2/binary")
                .filename("invalid_notice.pdf")
                .contentType("application/pdf")
                .size(2048L)
                .build()
        );
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/3")
                .binaryUrl("http://dm-store/doc/3/binary")
                .filename("hardship.pdf")
                .contentType("application/pdf")
                .size(4096L)
                .build()
        );
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetailsWhenNoNoticeOfRentChanges() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(Collections.emptyList())
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isNull();
        assertThat(result.getNoUploadOfNoticeProposingNewRentReason()).isNull();
        assertThat(result.getNoticeLegallyValid()).isNull();
        assertThat(result.getNoticeNotLegallyValidDetails()).isNull();
        assertThat(result.getRentIncreaseToCauseHardship()).isNull();
        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isNull();
        assertThat(result.getNoticeNotLegallyValidDocument()).isNull();
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isNull();
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetailsWhenNoDocuments() {
        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noticeLegallyValid(YesOrNo.YES)
            .rentIncreaseToCauseHardship(YesOrNo.NO)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(List.of(noticeOfRentChange))
            .documents(Collections.emptyList())
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoticeLegallyValid()).isEqualTo(YesOrNo.YES);
        assertThat(result.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.NO);
        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isNull();
        assertThat(result.getNoticeNotLegallyValidDocument()).isNull();
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isNull();
    }

    @Test
    public void shouldMapDocument() {
        DocumentEntity entity = DocumentEntity.builder()
            .url("http://dm-store/doc/1")
            .binaryUrl("http://dm-store/doc/1/binary")
            .fileName("file.pdf")
            .contentType("application/pdf")
            .size(5000L)
            .build();

        DocumentDto result = ApplicationMapper.mapDocument(entity);

        assertThat(result.getUrl()).isEqualTo("http://dm-store/doc/1");
        assertThat(result.getBinaryUrl()).isEqualTo("http://dm-store/doc/1/binary");
        assertThat(result.getFilename()).isEqualTo("file.pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getSize()).isEqualTo(5000L);
    }

    private static List<AddressEntity> addresses(String postcode) {
        return List.of(AddressEntity.builder().postcode(postcode).build());
    }

    private static List<TenancyDetailsEntity> tenancyDetails(TenancyType tenancyType) {
        return List.of(TenancyDetailsEntity.builder().tenancyType(tenancyType).build());
    }

    private static PTCaseEntity ptCase(List<AddressEntity> addresses, List<TenancyDetailsEntity> tenancyDetails) {
        return PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .hearingRequested(YesOrNo.YES)
            .propertyInspections(List.of(
                PropertyInspectionEntity.builder()
                    .agreeToDecisionWithoutInspection(YesOrNo.YES)
                    .noDecisionWithoutInspectionReason("Inspection reason")
                    .build()
            ))
            .addresses(addresses)
            .tenancyDetails(tenancyDetails)
            .build();
    }

    private static CasePartyEntity caseParty(
        PTCaseEntity ptCase,
        List<CasePartyAccessEntity> access,
        List<CasePartyContactPreferenceEntity> contactPreferences
    ) {
        return CasePartyEntity.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .organisationName(COMPANY_NAME)
            .referenceNumber(REFERENCE_NUMBER)
            .emailAddress(EMAIL)
            .phoneNumber(PHONE_NUMBER)
            .mobilePhoneNumber(MOBILE_NUMBER)
            .ptCase(ptCase)
            .access(access)
            .contactPreferences(contactPreferences)
            .build();
    }

    private static List<CasePartyContactPreferenceEntity> contactPreferences(YesOrNo text, YesOrNo phone) {
        return List.of(CasePartyContactPreferenceEntity.builder()
            .contactByText(text)
            .build());
    }

    private static CaseApplicationEntity entityWithCaseType(
        CasePartyEntity caseParty,
        ApplicationType applicationType
    ) {
        return CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(
                applicationType != null
                    ? CaseTypeEntity.builder().applicationTypeName(applicationType).build()
                    : null)
            .createdDate(CREATED_DATE)
            .build();
    }

    private static CaseApplicationEntity fullEntity(UUID userId) {
        PTCaseEntity ptCase = ptCase(addresses(POSTCODE), tenancyDetails(TENANCY_TYPE));
        List<CasePartyAccessEntity> access = List.of(
            CasePartyAccessEntity.builder().idamId(userId).build()
        );
        CasePartyEntity caseParty = caseParty(
            ptCase,
            access,
            contactPreferences(YesOrNo.YES, YesOrNo.NO)
        );
        return entityWithCaseType(caseParty, APPLICATION_TYPE);
    }
}
