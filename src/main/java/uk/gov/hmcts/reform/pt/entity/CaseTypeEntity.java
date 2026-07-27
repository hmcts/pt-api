package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_type")
public class CaseTypeEntity extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    private ApplicationType applicationTypeName;
}
