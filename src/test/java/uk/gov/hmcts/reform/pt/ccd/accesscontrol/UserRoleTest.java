package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRoleTest {
    @Test
    void shouldNotHaveNullRoleValues() {
        for (UserRole role : UserRole.values()) {
            assertNotNull(role.getRole());
            assertFalse(role.getRole().isBlank());
        }
    }

    @Test
    void shouldReturnCorrectCaseTypePermissionsAsString() {
        assertEquals(Permission.toString(Permission.CRU), UserRole.CREATOR.getCaseTypePermissions());
        assertEquals(Permission.toString(Set.of(Permission.R)), UserRole.RAS_VALIDATOR.getCaseTypePermissions());
    }

    @Test
    void idamRolesShouldHaveIdamRoleType() {
        Set<UserRole> idamRoles = Arrays.stream(UserRole.values())
            .filter(r -> r.getRoleType() == RoleType.IDAM)
            .collect(Collectors.toSet());

        assertTrue(idamRoles.contains(UserRole.CITIZEN));
        assertTrue(idamRoles.contains(UserRole.SYSTEM_USER));
        assertTrue(idamRoles.contains(UserRole.RAS_VALIDATOR));
    }

    @Test
    void rasRolesShouldHaveRasRoleType() {
        assertEquals(RoleType.RAS, UserRole.CREATOR.getRoleType());
        assertEquals(RoleType.RAS, UserRole.JUDGE.getRoleType());
    }

    @Test
    void shouldContainAccessProfiles() {
        assertNotNull(UserRole.HMCTS_ADMIN.getAccessProfiles());
        assertTrue(UserRole.HMCTS_ADMIN.getAccessProfiles().length > 0);
    }

    @Test
    void jsonValueShouldSerializeRoleField() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(UserRole.CITIZEN);

        assertTrue(json.contains("citizen"));
        assertFalse(json.contains("CITIZEN"));
    }

    @Test
    void allRolesShouldHavePermissionsDefined() {
        for (UserRole role : UserRole.values()) {
            assertNotNull(role.getCaseTypePermissions());
            assertFalse(role.getCaseTypePermissions().isEmpty());
        }
    }
}
