package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.reference.PartyRelationshipTypeEntity;

public interface PartyRelationshipTypeRepository extends JpaRepository<PartyRelationshipTypeEntity, String> {
}
