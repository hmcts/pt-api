package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.reference.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.repository.reference.CaseTypeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseTypeServiceTest {

    @Mock
    private CaseTypeRepository caseTypeRepository;

    @InjectMocks
    private CaseTypeService caseTypeService;

    @Test
    @DisplayName("Should return existing CaseTypeEntity if it exists")
    void getCaseTypeOrCreateIfNotExistsWhenExists() {
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;
        CaseTypeEntity existingCaseType = CaseTypeEntity.builder().valueEn(applicationType).build();
        when(caseTypeRepository.findFirstByValueEn(applicationType))
            .thenReturn(Optional.of(existingCaseType));

        CaseTypeEntity result = caseTypeService.getCaseTypeOrCreateIfNotExists(applicationType);

        assertThat(result).isEqualTo(existingCaseType);
        verify(caseTypeRepository).findFirstByValueEn(applicationType);
        verify(caseTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new CaseTypeEntity if it does not exist")
    void getCaseTypeOrCreateIfNotExistsWhenNotExists() {
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;
        when(caseTypeRepository.findFirstByValueEn(applicationType))
            .thenReturn(Optional.empty());
        when(caseTypeRepository.save(any(CaseTypeEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CaseTypeEntity result = caseTypeService.getCaseTypeOrCreateIfNotExists(applicationType);

        assertThat(result.getValueEn()).isEqualTo(applicationType);
        verify(caseTypeRepository).findFirstByValueEn(applicationType);
        verify(caseTypeRepository).save(any(CaseTypeEntity.class));
    }

    @Test
    @DisplayName("Should create CaseTypeEntity")
    void createCaseType() {
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;
        when(caseTypeRepository.save(any(CaseTypeEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CaseTypeEntity result = caseTypeService.createCaseType(applicationType);

        assertThat(result.getKey()).isEqualTo(applicationType.toString());
        assertThat(result.getValueEn()).isEqualTo(applicationType);
        verify(caseTypeRepository).save(any(CaseTypeEntity.class));
    }
}
