package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.PT_CASE_WORKER;

public class CaseworkerReadAccessTest {

    @Test
    void shouldGrantReadAccessToPtCaseWorkerOnly() {
        CaseworkerReadAccess access = new CaseworkerReadAccess();

        SetMultimap<HasRole, Permission> grants = access.getGrants();

        assertThat(grants.keySet())
            .containsExactly(PT_CASE_WORKER);

        assertThat(grants.get(PT_CASE_WORKER))
            .containsExactly(Permission.R);

        assertThat(grants.size()).isEqualTo(1);
    }
}
