package pl.volleylove.antenka.user.register;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.user.UserService;
import pl.volleylove.antenka.user.register.validators.interfaces.RegisterRequestValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RegisterRequestValidator validator;
    @InjectMocks
    private RegisterService registerService;

    //register data examples - RegisterValidator is mocked, but you can use it to get more clear tests
    private static final int AGE = 20;
    private static final String EMAIL = "user_test@test.com";
    private static final String PASS = "secretPassword1!";
    private static final String FIRST_NAME = "Firstname";
    private static final String LAST_NAME = "Firstname";
    private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(AGE); //adult

    private RegisterRequest getRegisterRequest() {
        return RegisterRequest.builder()
                .email(EMAIL)
                .password(PASS)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .birthday(BIRTHDAY)
                .build();
    }

    @Test
    void registerTestOk() {
        when(userService.findByEmail(any(String.class))).thenReturn(Optional.empty());

        RegisterResponse expectedResponse = RegisterResponse.builder()
                .email(EMAIL)
                .registerInfo(List.of(RegisterInfo.OK))
                .build();

        assertEquals(expectedResponse, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestRequestIsNullTest() {
        RegisterResponse expected = new RegisterResponse("", List.of(RegisterInfo.MISSING_INFO));

        //request is null
        when(validator.validateRequest(null)).thenReturn(List.of(RegisterInfo.MISSING_INFO));
        assertEquals(expected, registerService.register(null));

        //request has field(s) with nulls
        when(validator.validateRequest(any(RegisterRequest.class))).thenReturn(List.of(RegisterInfo.MISSING_INFO));
        assertEquals(expected, registerService.register(new RegisterRequest(null, PASS,
                FIRST_NAME, LAST_NAME, BIRTHDAY)));
        assertEquals(expected, registerService.register(new RegisterRequest(null, null,
                null, null, null)));

        //request has nulls, but not email
        RegisterResponse expectedWithEmail = new RegisterResponse(EMAIL, List.of(RegisterInfo.MISSING_INFO));
        assertEquals(expectedWithEmail, registerService.register(new RegisterRequest(EMAIL, null,
                null, null, null)));
    }


    @Test
    void registerTestIncorrectFirstName() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.INCORRECT_FIRST_NAME));

        RegisterResponse expectedIncorrectFirstName = RegisterResponse.builder()
                .email(EMAIL)
                .registerInfo(List.of(RegisterInfo.INCORRECT_FIRST_NAME))
                .build();

        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }


    @Test
    void registerTestIncorrectLastName() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.INCORRECT_LAST_NAME));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_LAST_NAME));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

    @Test
    void registerTestIncorrectFirstNameAndLastName() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.INCORRECT_FIRST_NAME, RegisterInfo.INCORRECT_LAST_NAME));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_FIRST_NAME, RegisterInfo.INCORRECT_LAST_NAME));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

    @Test
    void registerTestNotAllowedAge() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.NOT_ALLOWED_AGE));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.NOT_ALLOWED_AGE));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

    @Test
    void registerTestIncorrectEmail() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.INCORRECT_EMAIL));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_EMAIL));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

    @Test
    void registerTestPassDoNotMeetRequirements() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

    @Test
    void registerTestEveryRegisterDataIsIncorrect() {
        when(validator.validateRequest(any(RegisterRequest.class)))
                .thenReturn(List.of(RegisterInfo.INCORRECT_FIRST_NAME, RegisterInfo.INCORRECT_LAST_NAME, RegisterInfo.INCORRECT_EMAIL,
                        RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ, RegisterInfo.NOT_ALLOWED_AGE));


        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_FIRST_NAME, RegisterInfo.INCORRECT_LAST_NAME,
                        RegisterInfo.INCORRECT_EMAIL, RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ, RegisterInfo.NOT_ALLOWED_AGE));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestEmailIsNotUnique() {
        when(userService.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.EMAIL_ALREADY_EXISTS));

        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));
    }

}
