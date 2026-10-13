package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasRole;

import static java.util.Arrays.stream;

/**
 * Roles a user holds, either IDAM roles (e.g. citizen, caseworker-pt) or AM role
 * assignments (e.g. [DEFENDANT] on a case, judge across the service).
 *
 * <p>CCD maps these to {@link UserRole}s via RoleToAccessProfiles, and permissions
 * in the definition are granted against the access profiles.
 */
@AllArgsConstructor
@Getter
public enum UserRolesForAccessProfiles implements HasRole {

    CREATOR("[CREATOR]", "CRU"),
    DEFENDANT("[DEFENDANT]", "CRU"),
    DEFENDANT_SOLICITOR("[DEFENDANTSOLICITOR]", "CRU"),

    CITIZEN("idam:citizen", "CRU"),

    PT_CASE_WORKER("idam:caseworker-pt", "CRU"),
    PT_SOLICITOR("idam:caseworker-pt-solicitor", "CRU"),

    SUPER_USER("idam:caseworker-pt-superuser", "CRU"),
    SYSTEM_USER("idam:pt-system-update", "CRU"),

    RAS_VALIDATOR("idam:caseworker-ras-validation", "R"),

    CTSC_ADMIN("ctsc", "CRU"),
    HEARING_CENTRE_ADMIN("hearing-centre-admin", "CRU"),
    WLU_ADMIN("wlu-admin", "CRU"),

    FEE_PAID_JUDGE("fee-paid-judge", "R"),
    LEADERSHIP_JUDGE("leadership-judge", "R"),
    CIRCUIT_JUDGE("circuit-judge", "R"),
    JUDGE("judge", "R"),

    GS_PROFILE("GS_profile", "R");

    @JsonValue
    private final String role;
    private final String caseTypePermissions;

    public static String[] toRoles(UserRolesForAccessProfiles... profiles) {
        return stream(profiles)
            .map(UserRolesForAccessProfiles::getRole)
            .toArray(String[]::new);
    }
}
