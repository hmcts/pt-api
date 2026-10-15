package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.entity.CaseNotificationEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.NotificationType;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CaseNotificationRepositoryTest extends AbstractRepositoryTest<CaseNotificationRepository> {

    private final PTCaseRepository ptCaseRepository;

    private static final String TEST_USER_EMAIL = "user@example.com";

    protected CaseNotificationRepositoryTest(
        CaseNotificationRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    void saveAndFindByIdSuccess() {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(1234567890123456L)
            .build();
        ptCaseRepository.save(ptCase);

        UUID providerNotificationId = UUID.randomUUID();
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .ptCase(ptCase)
            .type(NotificationType.EMAIL)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .recipient(TEST_USER_EMAIL)
            .providerNotificationId(providerNotificationId)
            .build();

        CaseNotificationEntity saved = repository.save(notification);

        Optional<CaseNotificationEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getRecipient()).isEqualTo(TEST_USER_EMAIL);
        assertThat(result.get().getType()).isEqualTo(NotificationType.EMAIL);
        assertThat(result.get().getStatus()).isEqualTo(NotificationStatus.PENDING_SCHEDULE);
        assertThat(result.get().getProviderNotificationId()).isEqualTo(providerNotificationId);
        assertThat(result.get().getPtCase().getId()).isEqualTo(ptCase.getId());
    }
}
