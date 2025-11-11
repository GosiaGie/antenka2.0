package pl.volleylove.antenka.user.register;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.volleylove.antenka.user.register.validators.impl.RegisterValidatorImpl;
import pl.volleylove.antenka.user.register.validators.interfaces.DateTime;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterValidatorImplTest {

    @Mock
    private DateTime dateTime;

    @InjectMocks
    private RegisterValidatorImpl registerValidator;

    private static final LocalDate FIXED_DATE = LocalDate.of(2025,11,11);

    //isAgeCorrect() tests
    //current requirements - min: 16, max: 150
    private static Stream<Arguments> provideAge() {
        return Stream.of(
                Arguments.of(16, true),
                Arguments.of(15, false),
                Arguments.of(150, true),
                Arguments.of(151, false),
                Arguments.of(30, true),
                Arguments.of(-30, false),
                Arguments.of(0, false),
                Arguments.of(1000, false),
                Arguments.of(-1000, false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideAge")
    void isAgeCorrectTest(int age, boolean expected) {
        when(dateTime.getDate()).thenReturn(FIXED_DATE);

        //passing negative values result in future data
        assertEquals(expected, registerValidator.isAgeCorrect(LocalDate.now().minusYears(age)));

        verify(dateTime, times(2)).getDate();
    }

    //isNameFormatCorrect() tests
    //3 is minimum length, 20 is max
    private static Stream<Arguments> provideNames() {
        return Stream.of(
                Arguments.of("abc", true),
                Arguments.of( "ab1%343#", false),
                Arguments.of("ab", false),
                Arguments.of("dawerdfghtrfgrhgfdcf", true), //max lenght
                Arguments.of("tooooooooooloooooooog", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideNames")
    void isNameFormatCorrectTest(String name, boolean expected) {
        assertEquals(expected, registerValidator.isNameFormatCorrect(name));
    }

    //isNameFormatCorrect() tests
    //3 is minimum length, 20 is max
    private static Stream<Arguments> provideEmails() {
        return Stream.of(
                Arguments.of("abc", false),
                Arguments.of( "userwp.pl", false),
                Arguments.of("user@wp.pl", true),
                Arguments.of("ab", "ab", false),
                Arguments.of("%$#gosia@wp.pl", false),
                Arguments.of("gosia@wppl", false),
                Arguments.of("gosia123@wp.pl", true),
                Arguments.of( "              ", false),
                Arguments.of("aschshshenansnfnesnrnenjklodasqwerthgjfmikmgkpgfds@wp.pl", true), //max length of a local part - 50),
                Arguments.of("aschshshenansnfnesnrnenjklodasqwerthgjfmikmgkpgfdsaaaaaaaaaa@wp.pl", false), //max length of a local part - 50),
                Arguments.of("asca@wp.plplplplplplp", false)
        );
    }
    @ParameterizedTest
    @MethodSource("provideEmails")
    void isEmailFormatCorrectTest(String email, boolean expected) {
        assertEquals(expected, registerValidator.isEmailFormatCorrect(email));
    }


    //isPasswordFormatCorrect() tests
    private static Stream<Arguments> providePasswords() {
        return Stream.of(
                Arguments.of("abc", false),
                Arguments.of( "ab!abd", false),
                Arguments.of("dsad!sdsdsdas", false),
                Arguments.of("   ", false),
                Arguments.of("dfadw!4dsa", true),
                Arguments.of("!4#@#$@$@!#$!@$", false),
                Arguments.of( "              ", false),
                Arguments.of("a!3fdjklpolfgjklomngtrhbgnmlkopf", false)
        );
    }
    @ParameterizedTest
    @MethodSource("providePasswords")
    void isPasswordFormatCorrectTest(String password, boolean expected) {
        assertEquals(expected, registerValidator.isPasswordFormatCorrect(password));
    }

    @Test
    void registerValidatorMethodsGetNull() {
        assertFalse(registerValidator.isAgeCorrect(null));
        assertFalse(registerValidator.isNameFormatCorrect(null));
        assertFalse(registerValidator.isEmailFormatCorrect(null));
        assertFalse(registerValidator.isPasswordFormatCorrect(null));
    }

}
