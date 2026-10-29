package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefendantReadAccessTest {
    @Test
    void getGrants_ReturnsMultiMap() {
        DefendantReadAccess defendantReadAccess = new DefendantReadAccess();

        SetMultimap<HasRole, Permission> grants = defendantReadAccess.getGrants();

        assertThat(grants.get(UserRole.DEFENDANT_SOLICITOR)).containsExactly(Permission.R);
        assertThat(grants.get(UserRole.DEFENDANT)).containsExactly(Permission.R);
    }
}
