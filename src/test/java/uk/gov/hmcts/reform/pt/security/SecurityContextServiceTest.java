package uk.gov.hmcts.reform.pt.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.hmcts.reform.pt.exception.SecurityContextException;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityContextServiceTest {

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private User user;

    private MockedStatic<SecurityContextHolder> securityContextHolder;

    private SecurityContextService underTest;

    @BeforeEach
    void setUp() {
        securityContextHolder = mockStatic(SecurityContextHolder.class);
        securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        underTest = new SecurityContextService();
    }

    @AfterEach
    void tearDown() {
        securityContextHolder.close();
    }

    @Test
    @DisplayName("Should get the user details from the security context")
    void getUserDetails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        UserInfo expectedUserDetails = mock(UserInfo.class);
        when(user.getUserDetails()).thenReturn(expectedUserDetails);

        UserInfo actualUserDetails = underTest.getCurrentUserDetails();

        assertThat(actualUserDetails).isEqualTo(expectedUserDetails);
    }

    @Test
    @DisplayName("Should return null user details when no authentication in the security context")
    void getUserDetailsWhenNoAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Exception exception = catchException(() -> underTest.getCurrentUserDetails());

        assertThat(exception)
            .isInstanceOf(SecurityContextException.class)
            .hasMessage("No authentication instance found");
    }

    @Test
    @DisplayName("Should return null user details when no principal in the security context")
    void getUserDetailsWhenNoPrincipal() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        Exception exception = catchException(() -> underTest.getCurrentUserDetails());

        assertThat(exception)
            .isInstanceOf(SecurityContextException.class)
            .hasMessage("Authentication principal is null or not of the expected type");
    }

    @Test
    @DisplayName("Should return null user details for principal of wrong type in the security context")
    void getUserDetailsWhenPrincipalNotAUserType() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new Object());

        Exception exception = catchException(() -> underTest.getCurrentUserDetails());

        assertThat(exception)
            .isInstanceOf(SecurityContextException.class)
            .hasMessage("Authentication principal is null or not of the expected type");
    }

    @Test
    @DisplayName("Should get the user's auth token from the security context")
    void getAuthToken() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getAuthToken()).thenReturn("Bearer user-token");

        assertThat(underTest.getCurrentUserAuthToken()).isEqualTo("Bearer user-token");
    }

    @Test
    @DisplayName("Should throw for the auth token when no authentication in the security context")
    void getAuthTokenWhenNoAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Exception exception = catchException(() -> underTest.getCurrentUserAuthToken());

        assertThat(exception)
            .isInstanceOf(SecurityContextException.class)
            .hasMessage("No authentication instance found");
    }

    @Test
    @DisplayName("Should throw for the auth token when the principal is not a user")
    void getAuthTokenWhenPrincipalNotAUserType() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not a user");

        Exception exception = catchException(() -> underTest.getCurrentUserAuthToken());

        assertThat(exception).isInstanceOf(SecurityContextException.class);
    }
}
