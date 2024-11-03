package pl.volleylove.antenka.user.delete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import pl.volleylove.antenka.enums.DeleteAccountInfo;
import pl.volleylove.antenka.repository.UserRepository;
import pl.volleylove.antenka.security.UserPrincipal;
import pl.volleylove.antenka.security.UserPrincipalAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension .class)
class DeleteAccountServiceTest {

    private static final long ID = 123456789L;
    private static final String EMAIL = "test@test.com";
    private static final String NOT_USER_EMAIL = "not_user_email@test.com";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteAccountService deleteAccountService;

    private static final MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);

    @BeforeAll
    static void init() {
        securityContextHolder.when(SecurityContextHolder::getContext).thenAnswer(a -> {
            GrantedAuthority authority = () -> "ROLE_USER";

            UserPrincipal userPrincipal = UserPrincipal
                    .builder()
                    .userID(ID)
                    .email(EMAIL)
                    .authorities(List.of(authority))
                    .build();

            SecurityContext securityContext = new SecurityContextImpl();
            //Authentication's implementation: UserPrincipalAuthenticationToken
            securityContext.setAuthentication(new UserPrincipalAuthenticationToken(userPrincipal));
            return securityContext;
        });
    }

    @Test
    void deleteAccountAttemptTestEmailFromRequestIsNotEqualToEmailOfLoggedUser() {

        DeleteAccountResponse response = deleteAccountService.deleteAccountAttempt(NOT_USER_EMAIL);

        assertEquals(DeleteAccountInfo.INVALID_EMAIL, response.getInfo());

    }

    @Test
    void deleteAccountAttemptTestOk() {

        DeleteAccountResponse response = deleteAccountService.deleteAccountAttempt(EMAIL);

        assertEquals(DeleteAccountInfo.OK, response.getInfo());

    }

    
}
