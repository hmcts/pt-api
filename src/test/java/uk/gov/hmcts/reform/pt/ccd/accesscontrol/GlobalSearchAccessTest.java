package uk.gov.hmcts.reform.pt.ccd.accesscontrol;


import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static uk.gov.hmcts.ccd.sdk.api.Permission.R;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.GS_PROFILE;

class GlobalSearchAccessTest {

    private GlobalSearchAccess underTest;

    @BeforeEach
    void setUp() {
        underTest = new GlobalSearchAccess();
    }

    @Test
    void shouldGrantGlobalSearchAccess() {
        SetMultimap<HasRole, Permission> grants = underTest.getGrants();
        assertThat(grants.asMap()).contains(entry(GS_PROFILE, Set.of(R)));
    }

}
