package pl.volleylove.antenka.user.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.volleylove.antenka.security.JwtIssuer;
import pl.volleylove.antenka.security.UserPrincipal;
import pl.volleylove.antenka.security.UserPrincipalAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final long ID = 123456789L;
    private static final String TOKEN = "123456789";
    private static final String EMAIL = "test@test.com";
    private static final String PASS = "PASS";

    @Mock
    private JwtIssuer jwtIssuer;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private static final MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);

    //incorrect credentials, internal authentication error etc.
    @Test
    void loginAttemptTestAuthenticationFailed() {

        //user not found
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(a -> {
                    throw new UsernameNotFoundException("user not found");
                });

        try {
            authService.loginAttempt(EMAIL, PASS);
            fail();
        } catch (UsernameNotFoundException e) {
            verifyNoInteractions(jwtIssuer);
        }


        //internal exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(a -> {
                    throw new InternalAuthenticationServiceException("internal exception");
                });

        try {
            authService.loginAttempt(EMAIL, PASS);
            fail();
        } catch (InternalAuthenticationServiceException e) {
            verifyNoInteractions(jwtIssuer);
        }
    }

    @Test
    void loginAttemptTestSuccessfulAuthentication() {

        //result of authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(getAuthentication());
        securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(getSecurityContext());
        //token issuer
        when(jwtIssuer.issue(any(Long.class), any(String.class), ArgumentMatchers.anyList())).thenReturn(TOKEN);

        LoginResponse response = authService.loginAttempt(EMAIL, PASS);

        assertEquals(TOKEN, response.getAccessToken());

    }

    @Test
    void getAuthenticatedUserIDTest() {

        securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(getSecurityContext());

        assertEquals(ID, authService.getAuthenticatedUserID());

    }

    private UserPrincipalAuthenticationToken getAuthentication() {

        GrantedAuthority authority = () -> "ROLE_USER";

        UserPrincipal userPrincipal = UserPrincipal
                .builder()
                .userID(ID)
                .email(EMAIL)
                .authorities(List.of(authority))
                .build();

        return new UserPrincipalAuthenticationToken(userPrincipal);
    }

    private SecurityContext getSecurityContext() {

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(getAuthentication());
        return securityContext;
    }


}
