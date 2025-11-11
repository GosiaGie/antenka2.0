package pl.volleylove.antenka.user.register.validators.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.user.register.RegisterRequest;
import pl.volleylove.antenka.user.register.validators.interfaces.RegisterValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterRequestValidatorImplTest {

    private static String EMAIL = "emai@test.com";
    private static String PASS = "pass";
    private static String FIRST_NAME = "John";
    private static String LAST_NAME = "Smith";
    private static LocalDate BIRTHDAY = LocalDate.of(1990, 1,1);

    @Mock
    private RegisterValidator registerValidator;

    @InjectMocks
    private RegisterRequestValidatorImpl registerRequestValidator;

    @Test
    void noRegisterInfoFromValidatorTest() {
        when(registerValidator.isAgeCorrect(any(LocalDate.class))).thenReturn(true);
        when(registerValidator.isNameFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isEmailFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isPasswordFormatCorrect(any(String.class))).thenReturn(true);

        assertTrue(registerRequestValidator.validateRequest(RegisterRequest.builder()
                .email(EMAIL)
                .password(PASS)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .birthday(BIRTHDAY)
                .build()).isEmpty());

        verify(registerValidator, times(1)).isAgeCorrect(any(LocalDate.class));
        //first name + last name:
        verify(registerValidator, times(2)).isNameFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isEmailFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isPasswordFormatCorrect(any(String.class));
    }

    @Test
    void requestIsNullTest() {
        assertTrue(registerRequestValidator.validateRequest(null).contains(RegisterInfo.MISSING_INFO));

        verify(registerValidator, times(0)).isAgeCorrect(any(LocalDate.class));
        verify(registerValidator, times(0)).isNameFormatCorrect(any(String.class));
        verify(registerValidator, times(0)).isEmailFormatCorrect(any(String.class));
        verify(registerValidator, times(0)).isPasswordFormatCorrect(any(String.class));
    }

    private static Stream<Arguments> provideRequestData() {
        return Stream.of(
                Arguments.of(EMAIL, PASS, FIRST_NAME, LAST_NAME, null),
                Arguments.of(EMAIL, PASS, FIRST_NAME, null, null),
                Arguments.of(EMAIL, PASS, null, null, null),
                Arguments.of(EMAIL, null, null, null, null),
                Arguments.of(EMAIL, null, FIRST_NAME, LAST_NAME, BIRTHDAY),
                Arguments.of(EMAIL, PASS, null, LAST_NAME, BIRTHDAY),
                Arguments.of(EMAIL, PASS, FIRST_NAME, null, BIRTHDAY),
                Arguments.of(null, null, null, null, null),
                Arguments.of(EMAIL, null, FIRST_NAME, null, BIRTHDAY),
                Arguments.of(EMAIL, PASS, null, LAST_NAME, null),
                Arguments.of(null, PASS, FIRST_NAME, LAST_NAME, null),
                Arguments.of(EMAIL, null, null, null, BIRTHDAY)
        );
    }
    @ParameterizedTest
    @MethodSource("provideRequestData")
    void anyRequestFieldIsNullTest(String email, String pass, String firstName, String lastName, LocalDate birthday) {
        List<RegisterInfo> registerInfo = registerRequestValidator.validateRequest(RegisterRequest.builder()
                .email(email)
                .password(pass)
                .firstName(firstName)
                .lastName(lastName)
                .birthday(birthday)
                .build());

        assertTrue(registerInfo.contains(RegisterInfo.MISSING_INFO));
        assertEquals(1, registerInfo.size());
    }

    @Test
    void incorrectNameTest() {
        when(registerValidator.isAgeCorrect(any(LocalDate.class))).thenReturn(true);
        when(registerValidator.isNameFormatCorrect(any(String.class))).thenReturn(false);
        when(registerValidator.isEmailFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isPasswordFormatCorrect(any(String.class))).thenReturn(true);

        List<RegisterInfo> registerInfo = registerRequestValidator.validateRequest(RegisterRequest.builder()
                .email(EMAIL)
                .password(PASS)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .birthday(BIRTHDAY)
                .build());

        assertTrue(registerInfo.contains(RegisterInfo.INCORRECT_FIRST_NAME)
                && registerInfo.contains(RegisterInfo.INCORRECT_LAST_NAME)
                && registerInfo.size() == 2);

        verify(registerValidator, times(1)).isAgeCorrect(any(LocalDate.class));
        //first name + last name:
        verify(registerValidator, times(2)).isNameFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isEmailFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isPasswordFormatCorrect(any(String.class));
    }

    @Test
    void notAllowedAgeTest() {
        when(registerValidator.isAgeCorrect(any(LocalDate.class))).thenReturn(false);
        when(registerValidator.isNameFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isEmailFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isPasswordFormatCorrect(any(String.class))).thenReturn(true);

        List<RegisterInfo> registerInfo = registerRequestValidator.validateRequest(RegisterRequest.builder()
                .email(EMAIL)
                .password(PASS)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .birthday(BIRTHDAY)
                .build());

        assertTrue(registerInfo.contains(RegisterInfo.NOT_ALLOWED_AGE));
        assertTrue(registerInfo.size() == 1);

        verify(registerValidator, times(1)).isAgeCorrect(any(LocalDate.class));
        //first name + last name:
        verify(registerValidator, times(2)).isNameFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isEmailFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isPasswordFormatCorrect(any(String.class));
    }

    @Test
    void incorrectPassAndEmailTest() {
        when(registerValidator.isAgeCorrect(any(LocalDate.class))).thenReturn(true);
        when(registerValidator.isNameFormatCorrect(any(String.class))).thenReturn(true);
        when(registerValidator.isEmailFormatCorrect(any(String.class))).thenReturn(false);
        when(registerValidator.isPasswordFormatCorrect(any(String.class))).thenReturn(false);

        List<RegisterInfo> registerInfo = registerRequestValidator.validateRequest(RegisterRequest.builder()
                .email(EMAIL)
                .password(PASS)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .birthday(BIRTHDAY)
                .build());

        assertTrue(registerInfo.contains(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ) &&
                (registerInfo.contains(RegisterInfo.INCORRECT_EMAIL)));
        assertEquals(2, registerInfo.size());

        verify(registerValidator, times(1)).isAgeCorrect(any(LocalDate.class));
        //first name + last name:
        verify(registerValidator, times(2)).isNameFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isEmailFormatCorrect(any(String.class));
        verify(registerValidator, times(1)).isPasswordFormatCorrect(any(String.class));
    }
}