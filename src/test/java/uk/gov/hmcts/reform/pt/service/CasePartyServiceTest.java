package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyAccessRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyAddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CasePartyServiceTest {

    @Mock
    private CasePartyRepository casePartyRepository;

    @Mock
    private CasePartyAccessRepository casePartyAccessRepository;

    @Mock
    private CasePartyAddressRepository casePartyAddressRepository;

    @InjectMocks
    private CasePartyService casePartyService;

    @Test
    @DisplayName("Should return CasePartyEntity if it exists by idam id")
    void getCasePartyByIdamId() {
        UUID idamId = UUID.randomUUID();
        CasePartyEntity caseParty = CasePartyEntity.builder().firstName("John").build();
        when(casePartyRepository.findFirstByAccessIdamId(idamId)).thenReturn(Optional.of(caseParty));

        Optional<CasePartyEntity> result = casePartyService.getCasePartyByIdamId(idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        verify(casePartyRepository).findFirstByAccessIdamId(idamId);
    }

    @Test
    @DisplayName("Should create CasePartyEntity and related entities")
    void createCaseParty() {
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .email("john.doe@example.com")
            .postcode("AB12 3CD")
            .build();
        UUID idamId = UUID.randomUUID();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().caseReference(1234L).build();

        CasePartyEntity result = casePartyService.createCaseParty(ptCaseEntity, ptCase, idamId);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmailAddress()).isEqualTo("john.doe@example.com");

        verify(casePartyRepository).save(any(CasePartyEntity.class));
        verify(casePartyAddressRepository).save(any(AddressEntity.class));
        verify(casePartyAccessRepository).save(any(CasePartyAccessEntity.class));
    }
}
