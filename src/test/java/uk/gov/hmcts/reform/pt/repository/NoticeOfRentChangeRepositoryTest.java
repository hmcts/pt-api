package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeOfRentChangeRepositoryTest extends AbstractRepositoryTest<NoticeOfRentChangeRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected NoticeOfRentChangeRepositoryTest(
        NoticeOfRentChangeRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve notice of rent change entity")
    void saveAndFindNoticeOfRentChange() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .hearingRequested(YesOrNo.YES)
            .build();
        ptCaseRepository.save(ptCase);

        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("Hard copy only")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidDetails("Invalid notice date")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .ptCase(ptCase)
            .build();
        NoticeOfRentChangeEntity saved = repository.save(noticeOfRentChange);

        Optional<NoticeOfRentChangeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("Hard copy only");
        assertThat(result.get().getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(result.get().getNoticeNotLegallyValidDetails()).isEqualTo("Invalid notice date");
        assertThat(result.get().getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
