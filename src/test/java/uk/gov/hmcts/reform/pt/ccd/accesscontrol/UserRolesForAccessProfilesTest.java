package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRolesForAccessProfilesTest {

    @Test
    void shouldMapProfilesToTheirRoleStringsInOrder() {
        String[] roles = UserRolesForAccessProfiles.toRoles(
            UserRolesForAccessProfiles.CREATOR,
            UserRolesForAccessProfiles.CITIZEN
        );

        assertThat(roles).containsExactly("[CREATOR]", "idam:citizen");
    }

    @Test
    void shouldReturnEmptyArrayWhenNoProfilesGiven() {
        String[] roles = UserRolesForAccessProfiles.toRoles();

        assertThat(roles).isEmpty();
    }
}
