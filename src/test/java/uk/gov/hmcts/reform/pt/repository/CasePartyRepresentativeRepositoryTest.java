package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentativeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyRepresentativeRepositoryTest extends AbstractRepositoryTest<CasePartyRepresentativeRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;
    private final RepresentativeTypeRepository representativeTypeRepository;

    @Autowired
    protected CasePartyRepresentativeRepositoryTest(
        CasePartyRepresentativeRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository,
        RepresentativeTypeRepository representativeTypeRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
        this.representativeTypeRepository = representativeTypeRepository;
    }

    @Test
    @DisplayName("Should save and retrieve case party representative entity")
    void saveAndFindCasePartyRepresentative() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        CasePartyEntity party = casePartyRepository.save(
            CasePartyEntity.builder()
                .ptCase(ptCase)
                .firstName("John")
                .lastName("Smith")
                .build());

        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);

        CasePartyRepresentativeEntity representative = CasePartyRepresentativeEntity.builder()
            .startDate(startDate)
            .endDate(endDate)
            .isPrimary(YesOrNo.YES)
            .active(YesOrNo.YES)
            .party(party)
            .representativeType(representativeTypeRepository.save(
                RepresentativeTypeEntity.builder().typeName(RepresentativeType.LANDLORD_REPRESENTATIVE).build()))
            .firstName("Rep")
            .addressLine1("1 Rep Street")
            .townCity("Leeds")
            .postcode("LS1 1AA")
            .country("UK")
            .build();

        CasePartyRepresentativeEntity saved = repository.save(representative);

        Optional<CasePartyRepresentativeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getStartDate()).isEqualTo(startDate);
        assertThat(result.get().getEndDate()).isEqualTo(endDate);
        assertThat(result.get().getIsPrimary()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getActive()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getParty().getId()).isEqualTo(party.getId());
        assertThat(result.get().getFirstName()).isEqualTo("Rep");
        assertThat(result.get().getTownCity()).isEqualTo("Leeds");
        assertThat(result.get().getRepresentativeType().getTypeName())
            .isEqualTo(RepresentativeType.LANDLORD_REPRESENTATIVE);
    }
}
