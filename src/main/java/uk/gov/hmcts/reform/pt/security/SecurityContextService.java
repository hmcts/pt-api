package uk.gov.hmcts.reform.pt.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.exception.SecurityContextException;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;

@Service
public class SecurityContextService {

    public UserInfo getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new SecurityContextException("No authentication instance found");
        }

        if (authentication.getPrincipal() instanceof User user) {
            return user.getUserDetails();
        } else {
            throw new SecurityContextException("Authentication principal is null or not of the expected type");
        }
    }

    public String getCurrentUserAuthToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new SecurityContextException("No authentication instance found");
        }

        if (authentication.getPrincipal() instanceof User user) {
            return user.getAuthToken();
        } else {
            throw new SecurityContextException("Authentication principal is null or not of the expected type");
        }
    }
}
