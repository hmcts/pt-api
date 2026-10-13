package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

@Component
public class RoleToAccessProfiles implements CCDConfig<PTCase, State, UserRolesForAccessProfiles> {

    @Override
    public void configure(ConfigBuilder<PTCase, State, UserRolesForAccessProfiles> configBuilder) {
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CREATOR)
            .accessProfiles("[CREATOR]").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.DEFENDANT)
            .accessProfiles("[DEFENDANT]").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.DEFENDANT_SOLICITOR)
            .accessProfiles("[DEFENDANTSOLICITOR]").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CITIZEN)
            .accessProfiles("citizen").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.PT_CASE_WORKER)
            .accessProfiles("caseworker-pt").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.PT_SOLICITOR)
            .accessProfiles("caseworker-pt-solicitor").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.SUPER_USER)
            .accessProfiles("caseworker-pt-superuser", "GS_profile").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.SYSTEM_USER)
            .accessProfiles("pt-system-update").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.RAS_VALIDATOR)
            .accessProfiles("caseworker-ras-validation").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CTSC_ADMIN)
            .accessProfiles("ctsc").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.HEARING_CENTRE_ADMIN)
            .accessProfiles("hearing-centre-admin").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.WLU_ADMIN)
            .accessProfiles("wlu-admin").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.FEE_PAID_JUDGE)
            .accessProfiles("fee-paid-judge").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.LEADERSHIP_JUDGE)
            .accessProfiles("leadership-judge").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CIRCUIT_JUDGE)
            .accessProfiles("circuit-judge").build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.JUDGE)
            .accessProfiles("judge").build();
    }
}

