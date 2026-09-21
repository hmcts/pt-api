package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.SYSTEM_USER;

public class SystemUserAccessTest {

    @Test
    void shouldGrantCreateReadUpdateToSystemUserOnly() {
        SystemUserAccess access = new SystemUserAccess();

        SetMultimap<HasRole, Permission> grants = access.getGrants();

        assertThat(grants.keySet())
            .containsExactly(SYSTEM_USER);

        assertThat(grants.get(SYSTEM_USER))
            .containsExactlyInAnyOrderElementsOf(Permission.CRU);

        assertThat(grants.keySet()).hasSize(1);
    }

    @Test
    void shouldGrantTheRoleTheRetentionTaskAuthenticatesAs() {
        assertThat(SYSTEM_USER.getRole()).isEqualTo("pt-system-update");
    }
}
