package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddress;
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
    @DisplayName("Should return CaseParty if it exists by idam id")
    void getCasePartyByIdamId() {
        UUID idamId = UUID.randomUUID();
        CaseParty caseParty = CaseParty.builder().firstName("John").build();
        when(casePartyRepository.findFirstByAccessIdamId(idamId)).thenReturn(Optional.of(caseParty));

        Optional<CaseParty> result = casePartyService.getCasePartyByIdamId(idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
        verify(casePartyRepository).findFirstByAccessIdamId(idamId);
    }

    @Test
    @DisplayName("Should create CaseParty and related entities")
    void createCaseParty() {
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .email("john.doe@example.com")
            .postcode("AB12 3CD")
            .build();
        UUID idamId = UUID.randomUUID();

        CaseParty result = casePartyService.createCaseParty(ptCase, idamId);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmailAddress()).isEqualTo("john.doe@example.com");

        verify(casePartyRepository).save(any(CaseParty.class));
        verify(casePartyAddressRepository).save(any(CasePartyAddress.class));
        verify(casePartyAccessRepository).save(any(CasePartyAccess.class));
    }
}
