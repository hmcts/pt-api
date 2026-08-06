package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRoleTest {

    @Test
    void shouldHaveCorrectRoleValue() {
        assertEquals("caseworker-pt", UserRole.PT_CASE_WORKER.getRole());
    }

    @Test
    void shouldHaveCorrectCaseTypePermissions() {
        assertEquals(
            Permission.toString(Permission.CRU),
            UserRole.PT_CASE_WORKER.getCaseTypePermissions()
        );
    }

    @Test
    void shouldExposePermissionsAsString() {
        String permissions = UserRole.PT_CASE_WORKER.getCaseTypePermissions();

        assertNotNull(permissions);
        assertFalse(permissions.isBlank());
        assertTrue(permissions.contains("C"));
        assertTrue(permissions.contains("R"));
        assertTrue(permissions.contains("U"));
    }

    @Test
    void jsonValueShouldSerializeRoleField() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(UserRole.PT_CASE_WORKER);

        assertEquals("\"caseworker-pt\"", json);
    }
}
