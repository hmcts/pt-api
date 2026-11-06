package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.VettingInformationType;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationVettingEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.MissingInformationRequestEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationVettingRepository;
import uk.gov.hmcts.reform.pt.repository.MissingInformationRequestRepository;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseVettingServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private CaseApplicationVettingRepository caseApplicationVettingRepository;

    @Mock
    private MissingInformationRequestRepository missingInformationRequestRepository;

    @Mock
    private PTCaseService ptCaseService;

    @InjectMocks
    private CaseVettingService caseVettingService;

    @Test
    @DisplayName("Should throw IllegalStateException when MarketRentCaseEntity is null")
    void shouldThrowIllegalStateExceptionWhenMarketRentCaseIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .marketRentCase(null)
            .build();

        when(ptCaseService.getCase(CASE_REFERENCE)).thenReturn(ptCaseEntity);

        PTCase ptCase = PTCase.builder().build();

        assertThatThrownBy(() -> caseVettingService.updateCaseVetting(CASE_REFERENCE, ptCase))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Market rent case not found for case reference: " + CASE_REFERENCE);

        verify(caseApplicationVettingRepository, never()).save(any());
        verify(missingInformationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new CaseApplicationVettingEntity and save when none exists")
    void shouldCreateNewVettingEntityAndSaveWhenNoneExists() {
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .marketRentCase(marketRentCase)
            .build();

        when(ptCaseService.getCase(CASE_REFERENCE)).thenReturn(ptCaseEntity);
        when(caseApplicationVettingRepository.save(any(CaseApplicationVettingEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PTCase ptCase = PTCase.builder()
            .vettingInfoRequested(Collections.emptySet())
            .build();

        caseVettingService.updateCaseVetting(CASE_REFERENCE, ptCase);

        assertThat(marketRentCase.getCaseApplicationVetting()).isNotNull();
        assertThat(marketRentCase.getCaseApplicationVetting().getMarketRentCase()).isEqualTo(marketRentCase);
        verify(caseApplicationVettingRepository, times(2)).save(marketRentCase.getCaseApplicationVetting());
        verify(missingInformationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reuse existing CaseApplicationVettingEntity when present")
    void shouldReuseExistingVettingEntityWhenPresent() {
        CaseApplicationVettingEntity existingVetting = CaseApplicationVettingEntity.builder()
            .vettingStatus("IN_PROGRESS")
            .build();
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .caseApplicationVetting(existingVetting)
            .build();
        existingVetting.setMarketRentCase(marketRentCase);

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .marketRentCase(marketRentCase)
            .build();

        when(ptCaseService.getCase(CASE_REFERENCE)).thenReturn(ptCaseEntity);
        when(caseApplicationVettingRepository.save(any(CaseApplicationVettingEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PTCase ptCase = PTCase.builder()
            .vettingInfoRequested(Collections.emptySet())
            .build();

        caseVettingService.updateCaseVetting(CASE_REFERENCE, ptCase);

        assertThat(existingVetting.getMarketRentCase()).isEqualTo(marketRentCase);
        verify(caseApplicationVettingRepository, times(2)).save(existingVetting);
        verify(missingInformationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save MissingInformationRequestEntity when vettingInfoRequested is not empty")
    void shouldSaveMissingInformationRequestWhenVettingInfoRequestedIsNotEmpty() {
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder().build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .marketRentCase(marketRentCase)
            .build();

        when(ptCaseService.getCase(CASE_REFERENCE)).thenReturn(ptCaseEntity);
        when(caseApplicationVettingRepository.save(any(CaseApplicationVettingEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Set<VettingInformationType> requestedTypes = Set.of(
            VettingInformationType.SECTION_13_NOTICE,
            VettingInformationType.OTHER
        );
        PTCase ptCase = PTCase.builder()
            .vettingInfoRequested(requestedTypes)
            .vettingInfoOther("Missing proof of address description")
            .build();

        caseVettingService.updateCaseVetting(CASE_REFERENCE, ptCase);

        ArgumentCaptor<MissingInformationRequestEntity> captor =
            ArgumentCaptor.forClass(MissingInformationRequestEntity.class);
        verify(missingInformationRequestRepository).save(captor.capture());

        MissingInformationRequestEntity saved = captor.getValue();
        assertThat(saved.getMissingDocumentTypes()).containsExactlyInAnyOrder(
            VettingInformationType.SECTION_13_NOTICE,
            VettingInformationType.OTHER
        );
        assertThat(saved.getOtherDocumentsInformationDescription()).isEqualTo("Missing proof of address description");
        assertThat(saved.getCaseApplicationVetting()).isEqualTo(marketRentCase.getCaseApplicationVetting());
        assertThat(marketRentCase.getCaseApplicationVetting().getMissingInformationRequest()).isEqualTo(saved);
    }

    @Test
    @DisplayName("Should update existing MissingInformationRequestEntity when already present")
    void shouldUpdateExistingMissingInformationRequestWhenAlreadyPresent() {
        MissingInformationRequestEntity existingRequest = MissingInformationRequestEntity.builder()
            .missingDocumentTypes(Set.of(VettingInformationType.TENANCY_AGREEMENT))
            .otherDocumentsInformationDescription("Old description")
            .build();

        CaseApplicationVettingEntity vettingEntity = CaseApplicationVettingEntity.builder()
            .missingInformationRequest(existingRequest)
            .build();
        existingRequest.setCaseApplicationVetting(vettingEntity);

        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .caseApplicationVetting(vettingEntity)
            .build();
        vettingEntity.setMarketRentCase(marketRentCase);

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .marketRentCase(marketRentCase)
            .build();

        when(ptCaseService.getCase(CASE_REFERENCE)).thenReturn(ptCaseEntity);
        when(caseApplicationVettingRepository.save(any(CaseApplicationVettingEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Set<VettingInformationType> newTypes = Set.of(VettingInformationType.LANDLORD_NAME_AND_CONTACT_DETAILS);
        PTCase ptCase = PTCase.builder()
            .vettingInfoRequested(newTypes)
            .vettingInfoOther("Updated description")
            .build();

        caseVettingService.updateCaseVetting(CASE_REFERENCE, ptCase);

        verify(missingInformationRequestRepository).save(existingRequest);
        assertThat(existingRequest.getMissingDocumentTypes()).isEqualTo(newTypes);
        assertThat(existingRequest.getOtherDocumentsInformationDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should not overwrite existing otherDocumentsInformationDescription if vettingInfoOther is null")
    void shouldNotOverwriteDescriptionWhenVettingInfoOtherIsNull() {
        MissingInformationRequestEntity existingRequest = MissingInformationRequestEntity.builder()
            .missingDocumentTypes(Set.of(VettingInformationType.SECTION_13_NOTICE))
            .otherDocumentsInformationDescription("Existing description")
            .build();

        CaseApplicationVettingEntity vettingEntity = CaseApplicationVettingEntity.builder()
            .missingInformationRequest(existingRequest)
            .build();
        existingRequest.setCaseApplicationVetting(vettingEntity);

        PTCase ptCase = PTCase.builder()
            .vettingInfoRequested(Set.of(VettingInformationType.SECTION_13_NOTICE))
            .vettingInfoOther(null)
            .build();

        caseVettingService.updateMissingInfoRequest(vettingEntity, ptCase);

        verify(missingInformationRequestRepository).save(existingRequest);
        assertThat(existingRequest.getOtherDocumentsInformationDescription()).isEqualTo("Existing description");
    }
}
