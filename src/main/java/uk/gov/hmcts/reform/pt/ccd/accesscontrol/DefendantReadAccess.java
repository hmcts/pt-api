package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.gov.hmcts.ccd.sdk.api.HasAccessControl;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT_SOLICITOR;

public class DefendantReadAccess implements HasAccessControl {

    @Override
    public SetMultimap<HasRole, Permission> getGrants() {
        SetMultimap<HasRole, Permission> grants = HashMultimap.create();
        grants.put(RESPONDENT, Permission.R);
        grants.put(RESPONDENT_SOLICITOR, Permission.R);
        return grants;
    }
}

