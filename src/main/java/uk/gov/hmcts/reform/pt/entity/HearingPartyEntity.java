package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.pt.entity.reference.CustodyStatusEntity;
import uk.gov.hmcts.reform.pt.entity.reference.InterpreterLanguageEntity;
import uk.gov.hmcts.reform.pt.entity.reference.PartyRelationshipTypeEntity;
import uk.gov.hmcts.reform.pt.entity.reference.SignLanguageEntity;
import uk.gov.hmcts.reform.pt.entity.reference.UnavailableTypeEntity;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hearing_party")
public class HearingPartyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_hearing_id", nullable = false)
    @JsonBackReference
    private CaseHearingEntity caseHearing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_relationship_type_key", nullable = false)
    private PartyRelationshipTypeEntity partyRelationshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custody_status_key", nullable = false)
    private CustodyStatusEntity custodyStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interpreter_language_key", nullable = false)
    private InterpreterLanguageEntity interpreterLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sign_language_key", nullable = false)
    private SignLanguageEntity signLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unavailable_type_key", nullable = false)
    private UnavailableTypeEntity unavailableType;
}
