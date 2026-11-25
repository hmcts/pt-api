package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationVettingEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.LegalOfficerReferralEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class LegalOfficerReferralRepositoryTest extends AbstractRepositoryTest<LegalOfficerReferralRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePropertyDetailsRepository casePropertyDetailsRepository;
    private final CaseApplicationVettingRepository vettingRepository;

    protected LegalOfficerReferralRepositoryTest(LegalOfficerReferralRepository repository,
                                                  PTCaseRepository ptCaseRepository,
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
        LegalOfficerReferralEntity saved = repository.save(LegalOfficerReferralEntity.builder()
            .caseApplicationVetting(vetting)
            .urgency("Urgent")
            .referralReasons("Legal advice required")
            .referralDetails("Review the application")
            .recommendedInstructions("Contact the applicant")
            .lastModifiedBy("test user")
            .build());

        LegalOfficerReferralEntity result = repository.findById(saved.getId()).orElseThrow();
        assertThat(result.getCaseApplicationVetting().getId()).isEqualTo(vetting.getId());
        assertThat(result.getUrgency()).isEqualTo("Urgent");
        assertThat(result.getReferralReasons()).isEqualTo("Legal advice required");
        assertThat(result.getReferralDetails()).isEqualTo("Review the application");
        assertThat(result.getRecommendedInstructions()).isEqualTo("Contact the applicant");
        assertThat(result.getLastModifiedBy()).isEqualTo("test user");
        assertThat(result.getCreatedDate()).isNotNull();
        assertThat(result.getLastModifiedDate()).isNotNull();
    }
}
