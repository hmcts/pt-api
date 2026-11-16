package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentativeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.RepresentativeEntity;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentativeRepositoryTest extends AbstractRepositoryTest<RepresentativeRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;
    private final CasePartyRepresentativeRepository casePartyRepresentativeRepository;
    private final RepresentativeTypeRepository representativeTypeRepository;

    @Autowired
    protected RepresentativeRepositoryTest(
        RepresentativeRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository,
        CasePartyRepresentativeRepository casePartyRepresentativeRepository,
        RepresentativeTypeRepository representativeTypeRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
        this.casePartyRepresentativeRepository = casePartyRepresentativeRepository;
        this.representativeTypeRepository = representativeTypeRepository;
    }

    @Test
    @DisplayName("Should save and retrieve representative entity")
    void saveAndFindRepresentative() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        CasePartyEntity party = casePartyRepository.save(
            CasePartyEntity.builder()
                .ptCase(ptCase)
                .firstName("John")
                .lastName("Smith")
                .build());
        CasePartyRepresentativeEntity partyRep = casePartyRepresentativeRepository.save(
            CasePartyRepresentativeEntity.builder()
                .party(party)
                .primary(YesOrNo.YES)
                .active(YesOrNo.YES)
                .startDate(LocalDateTime.now())
                .build());
        RepresentativeTypeEntity repType = representativeTypeRepository.save(
            RepresentativeTypeEntity.builder().typeName("LETTING_AGENT").build());

        RepresentativeEntity rep = RepresentativeEntity.builder()
            .firstName("Alice")
            .lastName("Brown")
            .organisationName("Brown & Co")
            .emailAddress("alice@brownandco.com")
            .phoneNumber("02079460000")
            .casePartyRepresentative(partyRep)
            .representativeType(repType)
            .build();

        RepresentativeEntity saved = repository.save(rep);

        Optional<RepresentativeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getFirstName()).isEqualTo("Alice");
        assertThat(result.get().getLastName()).isEqualTo("Brown");
        assertThat(result.get().getOrganisationName()).isEqualTo("Brown & Co");
        assertThat(result.get().getEmailAddress()).isEqualTo("alice@brownandco.com");
        assertThat(result.get().getPhoneNumber()).isEqualTo("02079460000");
        assertThat(result.get().getCasePartyRepresentative().getId()).isEqualTo(partyRep.getId());
        assertThat(result.get().getRepresentativeType().getId()).isEqualTo(repType.getId());
    }
}
