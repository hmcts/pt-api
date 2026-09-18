package uk.gov.hmcts.reform.pt.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entity_role_code")
public class EntityRoleCodeEntity extends ReferenceDataEnCyEntity {
    private String roleName;
    private String parentKey;
    private String parentCategory;

    @Column(length = 100)
    private String lastModifiedBy;
}
