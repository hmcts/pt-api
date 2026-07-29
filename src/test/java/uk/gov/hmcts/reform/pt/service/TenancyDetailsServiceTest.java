package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TenancyDetailsServiceTest {

    @Mock
    private TenancyDetailsRepository tenancyDetailsRepository;

    @InjectMocks
    private TenancyDetailsService tenancyDetailsService;

    @Test
    @DisplayName("Should return existing TenancyDetailsEntity if it exists")
    void getTenancyDetailsOrCreateIfNotExistsWhenExists() {
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;
        long caseReference = 1234567890123456L;
        TenancyDetailsEntity existingTenancyDetails = TenancyDetailsEntity.builder().tenancyType(tenancyType).build();
        when(tenancyDetailsRepository.findFirstByTenancyTypeAndPtCase_CaseReference(tenancyType, caseReference))
            .thenReturn(Optional.of(existingTenancyDetails));

        TenancyDetailsEntity result =
            tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(tenancyType, caseReference);

        assertThat(result).isEqualTo(existingTenancyDetails);
        verify(tenancyDetailsRepository).findFirstByTenancyTypeAndPtCase_CaseReference(tenancyType, caseReference);
        verify(tenancyDetailsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new TenancyDetailsEntity if it does not exist")
    void getTenancyDetailsOrCreateIfNotExistsWhenNotExists() {
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;
        long caseReference = 1234567890123456L;
        when(tenancyDetailsRepository.findFirstByTenancyTypeAndPtCase_CaseReference(tenancyType, caseReference))
            .thenReturn(Optional.empty());
        when(tenancyDetailsRepository.save(any(TenancyDetailsEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        TenancyDetailsEntity result =
            tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(tenancyType, caseReference);

        assertThat(result.getTenancyType()).isEqualTo(tenancyType);
        verify(tenancyDetailsRepository).findFirstByTenancyTypeAndPtCase_CaseReference(tenancyType, caseReference);
        verify(tenancyDetailsRepository).save(any(TenancyDetailsEntity.class));
    }

    @Test
    @DisplayName("Should create TenancyDetailsEntity")
    void createTenancyDetails() {
        TenancyType tenancyType = TenancyType.ASSURED_PERIODIC_TENANCY;
        when(tenancyDetailsRepository.save(any(TenancyDetailsEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        TenancyDetailsEntity result = tenancyDetailsService.createTenancyDetails(tenancyType);

        assertThat(result.getTenancyType()).isEqualTo(tenancyType);
        verify(tenancyDetailsRepository).save(any(TenancyDetailsEntity.class));
    }
}
