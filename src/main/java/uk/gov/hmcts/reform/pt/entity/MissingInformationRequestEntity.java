package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.VettingInformationType;
import uk.gov.hmcts.reform.pt.entity.converter.VettingInformationTypeSetConverter;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "missing_information_request")
public class MissingInformationRequestEntity extends AuditableEntity {
    @Builder.Default
    @Column(name = "missing_information_types", length = 250)
    @Convert(converter = VettingInformationTypeSetConverter.class)
    private Set<VettingInformationType> missingDocumentTypes = EnumSet.noneOf(VettingInformationType.class);

    private String otherDocumentsInformationDescription;
    private LocalDateTime missingInformationDeadline;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_vetting_id", unique = true)
    @JsonBackReference
    private CaseApplicationVettingEntity caseApplicationVetting;
}
