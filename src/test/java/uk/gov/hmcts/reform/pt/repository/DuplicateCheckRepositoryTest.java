package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationVettingEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.DuplicateCheckEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateCheckRepositoryTest extends AbstractRepositoryTest<DuplicateCheckRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePropertyDetailsRepository casePropertyDetailsRepository;
    private final CaseApplicationVettingRepository vettingRepository;

    protected DuplicateCheckRepositoryTest(DuplicateCheckRepository repository, PTCaseRepository ptCaseRepository,
                                           CasePropertyDetailsRepository casePropertyDetailsRepository,
                                           CaseApplicationVettingRepository vettingRepository) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePropertyDetailsRepository = casePropertyDetailsRepository;
        this.vettingRepository = vettingRepository;
    }

    @Test
    void saveAndFindById() {
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(1234567890123456L).build());
        CasePropertyDetailsEntity details = casePropertyDetailsRepository.save(
            CasePropertyDetailsEntity.builder().ptCase(ptCase).build()
        );
        CaseApplicationVettingEntity vetting = vettingRepository.save(
            CaseApplicationVettingEntity.builder().casePropertyDetails(details).build()
        );
        DuplicateCheckEntity saved = repository.save(DuplicateCheckEntity.builder()
            .caseApplicationVetting(vetting)
            .lastModifiedBy("test user")
            .build());

        DuplicateCheckEntity result = repository.findById(saved.getId()).orElseThrow();
        assertThat(result.getCaseApplicationVetting().getId()).isEqualTo(vetting.getId());
        assertThat(result.getLastModifiedBy()).isEqualTo("test user");
        assertThat(result.getCreatedDate()).isNotNull();
        assertThat(result.getLastModifiedDate()).isNotNull();
    }
}
