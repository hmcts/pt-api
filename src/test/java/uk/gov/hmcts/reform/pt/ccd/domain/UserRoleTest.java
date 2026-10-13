package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void shouldHaveCorrectRoleValue() {
        assertThat(UserRole.PT_CASE_WORKER.getRole()).isEqualTo("caseworker-pt");
    }

    @Test
    void shouldHaveCorrectCaseTypePermissions() {
        assertThat(UserRole.PT_CASE_WORKER.getCaseTypePermissions())
            .isEqualTo(Permission.toString(Permission.CRU));
    }

    @Test
    void shouldExposePermissionsAsString() {
        String permissions = UserRole.PT_CASE_WORKER.getCaseTypePermissions();

        assertThat(permissions).isNotNull();
        assertThat(permissions).isNotBlank();
        assertThat(permissions).contains("C");
        assertThat(permissions).contains("R");
        assertThat(permissions).contains("U");
    }

    @Test
    void jsonValueShouldSerializeRoleField() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(UserRole.PT_CASE_WORKER);

        assertThat(json).isEqualTo("\"caseworker-pt\"");
    }
}
