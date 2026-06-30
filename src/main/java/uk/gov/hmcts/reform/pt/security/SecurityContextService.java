package uk.gov.hmcts.reform.pt.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pt.exception.SecurityContextException;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;

import java.util.UUID;

@Service
public class SecurityContextService {

    /**
     * Gets the current user ID from the {@link SecurityContext}.
     * @return The user ID for the user making the current request
     * @throws SecurityContextException if the security principal is not set or is not a {@link User} type
     */
    public UUID getCurrentUserId() {
        UserInfo userDetails = getCurrentUserDetails();
        return userDetails != null ? UUID.fromString(userDetails.getUid()) : null;
    }

    /**
     * Gets the current user details from the {@link SecurityContext}.
     * @return The user details for the user making the current request
     * @throws SecurityContextException if the security principal is not set or is not a {@link User} type
     */
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

}
