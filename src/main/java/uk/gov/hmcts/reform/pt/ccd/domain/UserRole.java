package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.RoleType;

import java.util.Set;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.ccd.sdk.api.Permission.R;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.RoleType.IDAM;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.RoleType.RAS;

/**
 * All the different roles for a PT case.
 */
@AllArgsConstructor
@Getter
public enum UserRole implements HasRole {

    PT_CASE_WORKER("caseworker-pt", CRU, IDAM),
    PT_SOLICITOR("caseworker-pt-solicitor", CRU, IDAM),
    CITIZEN("citizen", CRU, IDAM),
    DEFENDANT("[DEFENDANT]", CRU, RAS),
    DEFENDANT_SOLICITOR("[DEFENDANTSOLICITOR]", CRU, RAS),
    RAS_VALIDATOR("caseworker-ras-validation", Set.of(R), IDAM),
    CTSC_ADMIN("ctsc", Permission.CRU, RAS),
    HEARING_CENTRE_ADMIN("hearing-centre-admin", Permission.CRU, RAS),
    WLU_ADMIN("wlu-admin", Permission.CRU, RAS),
    FEE_PAID_JUDGE("fee-paid-judge", Set.of(R), RAS),
    LEADERSHIP_JUDGE("leadership-judge", Set.of(R), RAS),
    CIRCUIT_JUDGE("circuit-judge", Set.of(R), RAS),
    JUDGE("judge", Set.of(R), RAS),
    SUPER_USER("caseworker-pt-superuser", CRU, IDAM);

    @JsonValue
    private final String role;
    private final Set<Permission> caseTypePermissions;
    private final RoleType roleType;


    public String getCaseTypePermissions() {
        return Permission.toString(caseTypePermissions);
    }
}
