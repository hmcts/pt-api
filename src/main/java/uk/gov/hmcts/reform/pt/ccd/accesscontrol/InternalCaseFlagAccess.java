package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.gov.hmcts.ccd.sdk.api.HasAccessControl;
import uk.gov.hmcts.ccd.sdk.api.HasRole;
import uk.gov.hmcts.ccd.sdk.api.Permission;

import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.CIRCUIT_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.CTSC_ADMIN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.FEE_PAID_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.HEARING_CENTRE_ADMIN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.LEADERSHIP_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile.WLU_ADMIN;


public class InternalCaseFlagAccess implements HasAccessControl {

    @Override
    public SetMultimap<HasRole, Permission> getGrants() {
        SetMultimap<HasRole, Permission> grants = HashMultimap.create();
        grants.putAll(CTSC_ADMIN, Permission.CRU);
        grants.putAll(HEARING_CENTRE_ADMIN, Permission.CRU);
        grants.putAll(WLU_ADMIN, Permission.CRU);
        grants.put(FEE_PAID_JUDGE, Permission.R);
        grants.put(CIRCUIT_JUDGE, Permission.R);
        grants.put(LEADERSHIP_JUDGE, Permission.R);
        grants.put(JUDGE, Permission.R);

        return grants;
    }
}
