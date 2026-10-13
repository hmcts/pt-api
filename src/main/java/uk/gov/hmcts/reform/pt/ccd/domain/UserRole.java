package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRolesForAccessProfiles;

/**
 * Access profiles used in the CCD definition when granting permissions on the case type,
 * events, states and fields.
 *
 * <p>Users get access profiles through their {@link UserRolesForAccessProfiles}s via RoleToAccessProfiles.
 * Usually it's one-to-one with the same name, but several roles can share a profile
 * (e.g. GS_profile for global search) and one role can map to several profiles.
 *
 * <p>If RoleToAccessProfiles is empty, CCD maps each role to a profile of the same name.
 * Once any mapping is added, every role needs one.
 */
@AllArgsConstructor
@Getter
public enum UserRole implements HasRole {

    CREATOR("[CREATOR]", "CRU"),
    DEFENDANT("[DEFENDANT]", "CRU"),
    DEFENDANT_SOLICITOR("[DEFENDANTSOLICITOR]", "CRU"),

    CITIZEN("citizen", "CRU"),

    PT_CASE_WORKER("caseworker-pt", "CRU"),
    PT_SOLICITOR("caseworker-pt-solicitor", "CRU"),

    SUPER_USER("caseworker-pt-superuser", "CRU"),
    SYSTEM_USER("pt-system-update", "CRU"),

    RAS_VALIDATOR("caseworker-ras-validation", "R"),

    HEARING_CENTRE_ADMIN("hearing-centre-admin", "CRU"),

    FEE_PAID_JUDGE("fee-paid-judge", "R"),
    LEADERSHIP_JUDGE("leadership-judge", "R"),
    CIRCUIT_JUDGE("circuit-judge", "R"),
    JUDGE("judge", "R"),

    GS_PROFILE("GS_profile", "R");

    @JsonValue
    private final String role;
    private final String caseTypePermissions;
}
