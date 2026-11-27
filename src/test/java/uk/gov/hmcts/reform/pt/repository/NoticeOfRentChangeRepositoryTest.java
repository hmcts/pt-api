package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeOfRentChangeRepositoryTest extends AbstractRepositoryTest<NoticeOfRentChangeRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePropertyDetailsRepository casePropertyDetailsRepository;

    @Autowired
    protected NoticeOfRentChangeRepositoryTest(
        NoticeOfRentChangeRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePropertyDetailsRepository casePropertyDetailsRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePropertyDetailsRepository = casePropertyDetailsRepository;
    }

    @Test
    @DisplayName("Should save and retrieve notice of rent change entity")
    void saveAndFindNoticeOfRentChange() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();
        ptCaseRepository.save(ptCase);
        CasePropertyDetailsEntity casePropertyDetails = casePropertyDetailsRepository.save(
            CasePropertyDetailsEntity.builder().ptCase(ptCase).build()
        );

        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("Hard copy only")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidReason("Invalid notice date")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .rentIncreaseToCauseHardshipDetails("Financial hardship")
            .casePropertyDetails(casePropertyDetails)
            .build();
        NoticeOfRentChangeEntity saved = repository.save(noticeOfRentChange);

        Optional<NoticeOfRentChangeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("Hard copy only");
        assertThat(result.get().getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(result.get().getNoticeNotLegallyValidReason()).isEqualTo("Invalid notice date");
        assertThat(result.get().getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getRentIncreaseToCauseHardshipDetails()).isEqualTo("Financial hardship");
        assertThat(result.get().getCasePropertyDetails().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
