package uk.gov.hmcts.reform.pt.ccd.config;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.dse.ccd.CcdEnvironment;
import uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig;
import uk.gov.hmcts.befta.dse.ccd.DataLoaderToDefinitionStore;
import uk.gov.hmcts.befta.exception.ImportException;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.reform.pt.ccd.CaseType;

import javax.crypto.AEADBadTagException;
import javax.net.ssl.SSLException;
import java.util.List;
import java.util.Locale;

import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CIRCUIT_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.FEE_PAID_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.GS_PROFILE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.HEARING_CENTRE_ADMIN;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.LEADERSHIP_JUDGE;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.PT_SOLICITOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RAS_VALIDATOR;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.SUPER_USER;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.SYSTEM_USER;


public class HighLevelDataSetupApp extends DataLoaderToDefinitionStore {

    private static final Logger logger = LoggerFactory.getLogger(HighLevelDataSetupApp.class);
    private static final String PUBLIC = "PUBLIC";

    private static final CcdRoleConfig[] CCD_ROLES = {
        new CcdRoleConfig("caseworker", PUBLIC),
        new CcdRoleConfig(PT_CASE_WORKER.getRole(), PUBLIC),
        new CcdRoleConfig(PT_SOLICITOR.getRole(), PUBLIC),
        new CcdRoleConfig(RAS_VALIDATOR.getRole(), PUBLIC),
        new CcdRoleConfig(CITIZEN.getRole(), PUBLIC),
        new CcdRoleConfig(GS_PROFILE.getRole(), PUBLIC),
        new CcdRoleConfig(HEARING_CENTRE_ADMIN.getRole(), PUBLIC),
        new CcdRoleConfig(JUDGE.getRole(), PUBLIC),
        new CcdRoleConfig(FEE_PAID_JUDGE.getRole(), PUBLIC),
        new CcdRoleConfig(CIRCUIT_JUDGE.getRole(), PUBLIC),
        new CcdRoleConfig(LEADERSHIP_JUDGE.getRole(), PUBLIC),
        new CcdRoleConfig(SYSTEM_USER.getRole(), PUBLIC),
        new CcdRoleConfig(SUPER_USER.getRole(), PUBLIC)
    };

    private final CcdEnvironment environment;

    public HighLevelDataSetupApp(CcdEnvironment dataSetupEnvironment) {
        super(dataSetupEnvironment);
        environment = dataSetupEnvironment;
    }

    public static void main(String[] args) throws Throwable {
        DataLoaderToDefinitionStore.main(HighLevelDataSetupApp.class, args);
    }

    @Override
    public void addCcdRoles() {
        for (CcdRoleConfig roleConfig : CCD_ROLES) {
            try {
                logger.info("\n\nAdding CCD Role {}.", roleConfig);
                addCcdRole(roleConfig);
                logger.info("\n\nAdded CCD Role {}.", roleConfig);
            } catch (Exception e) {
                logger.error("\n\nCouldn't add CCD Role {}.\n\n", roleConfig, e);
                if (!shouldTolerateDataSetupFailure()) {
                    throw e;
                }
            }
        }
    }

    @Override
    protected List<String> getAllDefinitionFilesToLoadAt(String definitionsPath) {
        String environmentName = environment.name().toLowerCase(Locale.UK);
        List<String> files = new java.util.ArrayList<>(
            List.of("build/definitions/CCD_Definition_" + CaseType.getCaseType()
                        + "_" + environmentName + ".xlsx")
        );
        if ("aat".equals(environmentName)) {
            files.add("build/definitions/CCD_Definition_" + CaseType.getCaseType()
                          + "-staging_" + environmentName + ".xlsx");
        }
        return files;
    }

    @Override
    public void createRoleAssignments() {
        // Do not create role assignments.
        BeftaUtils.defaultLog("Will NOT create role assignments!");
    }

    @Override
    protected boolean shouldTolerateDataSetupFailure(Throwable e) {
        return switch (e) {
            case ImportException importException -> isGatewayTimeout(importException);
            case SSLException sslException -> true;
            case AEADBadTagException aeadBadTagException -> true;
            default -> shouldTolerateDataSetupFailure();
        };
    }

    private boolean isGatewayTimeout(ImportException importException) {
        return importException.getHttpStatusCode() == HttpStatus.SC_GATEWAY_TIMEOUT;
    }
}
