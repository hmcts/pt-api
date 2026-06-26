package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pt_case")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Version
    private Integer version;
    private Long caseReference;
    private String applicantForename;
}
