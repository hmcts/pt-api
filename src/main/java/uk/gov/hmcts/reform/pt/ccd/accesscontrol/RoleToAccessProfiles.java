package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CIRCUIT_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CREATOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.GS_PROFILE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.FEE_PAID_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.HEARING_CENTRE_ADMIN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.LEADERSHIP_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.PT_SOLICITOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RAS_VALIDATOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT_SOLICITOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.SUPER_USER;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.SYSTEM_USER;

@Component
public class RoleToAccessProfiles implements CCDConfig<PTCase, State, UserRolesForAccessProfiles> {

    @Override
    public void configure(ConfigBuilder<PTCase, State, UserRolesForAccessProfiles> configBuilder) {
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CREATOR)
            .accessProfiles(CREATOR.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.RESPONDENT)
            .accessProfiles(RESPONDENT.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.RESPONDENT_SOLICITOR)
            .accessProfiles(RESPONDENT_SOLICITOR.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CITIZEN)
            .accessProfiles(CITIZEN.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.PT_CASE_WORKER)
            .accessProfiles(PT_CASE_WORKER.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.PT_SOLICITOR)
            .accessProfiles(PT_SOLICITOR.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.SUPER_USER)
            .accessProfiles(SUPER_USER.getRole(), GS_PROFILE.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.SYSTEM_USER)
            .accessProfiles(SYSTEM_USER.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.RAS_VALIDATOR)
            .accessProfiles(RAS_VALIDATOR.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CTSC_ADMIN)
            .accessProfiles(GS_PROFILE.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.HEARING_CENTRE_ADMIN)
            .accessProfiles(HEARING_CENTRE_ADMIN.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.FEE_PAID_JUDGE)
            .accessProfiles(FEE_PAID_JUDGE.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.LEADERSHIP_JUDGE)
            .accessProfiles(LEADERSHIP_JUDGE.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.CIRCUIT_JUDGE)
            .accessProfiles(CIRCUIT_JUDGE.getRole()).build();
        configBuilder.caseRoleToAccessProfile(UserRolesForAccessProfiles.JUDGE)
            .accessProfiles(JUDGE.getRole()).build();
    }
}

