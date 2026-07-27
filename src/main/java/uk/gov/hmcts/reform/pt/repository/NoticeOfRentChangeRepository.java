package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;

public interface NoticeOfRentChangeRepository extends JpaRepository<NoticeOfRentChangeEntity, Long> {
}
