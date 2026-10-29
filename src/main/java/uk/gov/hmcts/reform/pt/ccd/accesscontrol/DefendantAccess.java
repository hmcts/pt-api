package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.gov.hmcts.ccd.sdk.api.HasAccessControl;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT_SOLICITOR;

public class DefendantAccess implements HasAccessControl {

    @Override
    public SetMultimap<HasRole, Permission> getGrants() {
        SetMultimap<HasRole, Permission> grants = HashMultimap.create();
        grants.putAll(RESPONDENT, Permission.CRU);
        grants.putAll(RESPONDENT_SOLICITOR, Permission.CRU);
        return grants;
    }
}
