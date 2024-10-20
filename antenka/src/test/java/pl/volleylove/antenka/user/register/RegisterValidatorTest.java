package pl.volleylove.antenka.user.register;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class RegisterValidatorTest {

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

        //passing negative values results future data
        assertEquals(expected, RegisterValidator.isAgeCorrect(LocalDate.now().minusYears(age)));

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

        assertEquals(expected, RegisterValidator.isNameFormatCorrect(name));

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

        assertEquals(expected, RegisterValidator.isEmailFormatCorrect(email));

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

        assertEquals(expected, RegisterValidator.isPasswordFormatCorrect(password));

    }

    @Test
    void registerValidatorMethodsGetNull() {
        assertFalse(RegisterValidator.isAgeCorrect(null));

        assertFalse(RegisterValidator.isNameFormatCorrect(null));

        assertFalse(RegisterValidator.isEmailFormatCorrect(null));

        assertFalse(RegisterValidator.isPasswordFormatCorrect(null));

    }

}
