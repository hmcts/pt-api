package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Version;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String postcode;
    private String applicationType;
}
