package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CITIZEN;

public class CitizenAccessTest {

    @Test
    void shouldGrantFullCrudAccessToCitizen() {
        CitizenAccess access = new CitizenAccess();

        SetMultimap<HasRole, Permission> grants = access.getGrants();

        assertThat(grants.keySet())
            .containsExactly(CITIZEN);

        assertThat(grants.get(CITIZEN))
            .containsExactlyInAnyOrder(
                Permission.C,
                Permission.R,
                Permission.U,
                Permission.D
            );

        assertThat(grants.size()).isEqualTo(4);
    }
}
