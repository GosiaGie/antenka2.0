package pl.volleylove.antenka.user.register;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegisterService registerService;
    private static final MockedStatic<RegisterValidator> registerValidator = mockStatic(RegisterValidator.class);


    //register data examples - RegisterValidator is mocked, but you can use it to get more clear tests
    private static final int AGE = 20;
    private static final String EMAIL = "user_test@test.com";
    private static final String PASS = "secretPassword1!";
    private static final String FIRST_NAME = "Firstname";
    private static final String LAST_NAME = "Firstname";
    private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(AGE); //adult

    @BeforeEach
    public void init() {

        //true is default return value for data validator
        registerValidator.when(() -> RegisterValidator.isNameFormatCorrect(any(String.class)))
                .thenReturn(true);
        registerValidator.when(() -> RegisterValidator.isEmailFormatCorrect(any(String.class)))
                .thenReturn(true);
        registerValidator.when(() -> RegisterValidator.isPasswordFormatCorrect(any(String.class)))
                .thenReturn(true);
        registerValidator.when(() -> RegisterValidator.isAgeCorrect(any(LocalDate.class)))
                .thenReturn(true);

    }

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

        RegisterResponse expected = new RegisterResponse(null, List.of(RegisterInfo.MISSING_INFO));

        assertEquals(expected, registerService.register(null));

        assertEquals(expected, registerService.register(new RegisterRequest(null, PASS,
                FIRST_NAME, LAST_NAME, BIRTHDAY)));

        assertEquals(expected, registerService.register(new RegisterRequest(null, null,
                null, null, null)));

        assertEquals(expected, registerService.register(new RegisterRequest(EMAIL, null,
                null, null, null)));

    }


    @Test
    void registerTestIncorrectFirstName() {

        registerValidator.when(() -> RegisterValidator.isNameFormatCorrect(any(String.class)))
                .thenReturn(false) //first name - incorrect
                .thenReturn(true); //last name - correct

        RegisterResponse expectedIncorrectFirstName = RegisterResponse.builder()
                .email(EMAIL)
                .registerInfo(List.of(RegisterInfo.INCORRECT_FIRST_NAME))
                .build();

        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }


    @Test
    void registerTestIncorrectLastName() {

        registerValidator.when(() -> RegisterValidator.isNameFormatCorrect(any(String.class)))
                .thenReturn(true) //first name - correct
                .thenReturn(false); //last name - incorrect

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_LAST_NAME));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestIncorrectFirstNameAndLastName() {

        registerValidator.when(() -> RegisterValidator.isNameFormatCorrect(any(String.class)))
                .thenReturn(false) //first name - incorrect
                .thenReturn(false); //last name - incorrect

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_FIRST_NAME, RegisterInfo.INCORRECT_LAST_NAME));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestNotAllowedAge() {

        registerValidator.when(() -> RegisterValidator.isAgeCorrect(any(LocalDate.class)))
                .thenReturn(false); //first name - incorrect

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.NOT_ALLOWED_AGE));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestIncorrectEmail() {

        registerValidator.when(() -> RegisterValidator.isEmailFormatCorrect(any(String.class)))
                .thenReturn(false); //first name - incorrect

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.INCORRECT_EMAIL));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestPassDoNotMeetRequirements() {

        registerValidator.when(() -> RegisterValidator.isPasswordFormatCorrect(any(String.class)))
                .thenReturn(false); //first name - incorrect

        RegisterResponse expectedIncorrectFirstName =
                new RegisterResponse(EMAIL, List.of(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ));
        assertEquals(expectedIncorrectFirstName, registerService.register(getRegisterRequest()));

    }

    @Test
    void registerTestEveryRegisterDataIsIncorrect() {
        registerValidator.when(() -> RegisterValidator.isNameFormatCorrect(any(String.class)))
                .thenReturn(false);
        registerValidator.when(() -> RegisterValidator.isEmailFormatCorrect(any(String.class)))
                .thenReturn(false);
        registerValidator.when(() -> RegisterValidator.isPasswordFormatCorrect(any(String.class)))
                .thenReturn(false);
        registerValidator.when(() -> RegisterValidator.isAgeCorrect(any(LocalDate.class)))
                .thenReturn(false);


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
