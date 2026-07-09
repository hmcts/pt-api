package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PTCaseServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private PTCaseRepository ptCaseRepository;

    @InjectMocks
    private PTCaseService underTest;

    @Test
    void shouldGetApplicationsForUser() {
        PTCaseEntity entity = createPtCase();
        CaseDto dto = createApplicationDto(entity);

        when(ptCaseRepository.findAllByIdamUserId(entity.getIdamUserId()))
            .thenReturn(List.of(entity));

        List<CaseDto> result = underTest.getApplicationsForUser(entity.getIdamUserId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(dto.getId());

        verify(ptCaseRepository).findAllByIdamUserId(entity.getIdamUserId());
        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    void shouldGetApplicationByCaseReference() {
        PTCaseEntity entity = createPtCase();
        CaseDto dto = createApplicationDto(entity);
        UUID idamUserId = entity.getIdamUserId();

        when(ptCaseRepository.findByCaseReferenceAndIdamUserId(CASE_REFERENCE, idamUserId))
            .thenReturn(Optional.of(entity));

        CaseDto result = underTest.getApplicationByCaseReference(CASE_REFERENCE, idamUserId);

        assertThat(result.getId()).isEqualTo(dto.getId());

        verify(ptCaseRepository).findByCaseReferenceAndIdamUserId(CASE_REFERENCE, idamUserId);
        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    void shouldThrowInvalidCaseReferenceWhenCaseReferenceIsZero() {
        assertThatThrownBy(() -> underTest.getApplicationByCaseReference(0L, UUID.randomUUID()))
            .isInstanceOf(InvalidCaseReferenceException.class)
            .hasMessage("Invalid case reference: 0");

        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    void shouldThrowCaseNotFoundWhenCaseReferenceDoesNotExist() {

        when(ptCaseRepository.findByCaseReferenceAndIdamUserId(eq(CASE_REFERENCE), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getApplicationByCaseReference(CASE_REFERENCE, UUID.randomUUID()))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessage("No case found with reference " + CASE_REFERENCE);

        verify(ptCaseRepository).findByCaseReferenceAndIdamUserId(eq(CASE_REFERENCE), any());
        verifyNoMoreInteractions(ptCaseRepository);
    }

    private static PTCaseEntity createPtCase() {
        return PTCaseEntity.builder()
            .id(UUID.randomUUID())
            .caseReference(CASE_REFERENCE)
            .idamUserId(UUID.randomUUID())
            .build();
    }

    private static CaseDto createApplicationDto(PTCaseEntity entity) {
        return CaseDto.builder()
            .id(entity.getId())
            .caseReference(entity.getCaseReference())
            .build();
    }
}
