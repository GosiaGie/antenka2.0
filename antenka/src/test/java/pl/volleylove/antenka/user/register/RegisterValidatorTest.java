package pl.volleylove.antenka.user.register;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

@RunWith(JUnitParamsRunner.class)
public class RegisterValidatorTest {
    @Test
    @Parameters({"2, false", "-2, false", "0, false", "20, true", "100, true", "150, true", "16, true", "0, false"})
    public void isAgeCorrectTestPastDates(int age, boolean expected) {

        //current requirements - min: 16, max: 150
        Assert.assertEquals(expected, RegisterValidator.isAgeCorrect(LocalDate.now().minusYears(age)));

    }

    @Test
    @Parameters({"2", "1000", "0"})
    public void isAgeCorrectTestFutureDates(int age) {

        Assert.assertFalse(RegisterValidator.isAgeCorrect(LocalDate.now().plusYears(age)));

    }

    @Test
    @Parameters({"abc, abc, true", "ab1%343#, abd, false" , "dddadse, !^abc4, false", "ab, ab, false", "ab, ab, false", "abc, abc, true",
    "abcabcabcabcabcabcab, abcabcabcabcabcabcab, true", ", , false"})
    public void isNameFormatCorrect(String firstName, String lastName, boolean expected) {

        Assert.assertEquals(expected, RegisterValidator.isNameFormatCorrect(firstName, lastName));

    }

    @Test
    @Parameters({"abc, false", "gosiawp.pl, false", "gosia@wp.pl, true", "%$#gosia@wp.pl, false", "gosia@wppl, false",
            "gosia123@wp.pl, true", " , false", "aschshshenansnfnesnrnenjklodasqwerthgjfmikmgkpgfds@wp.pl, true", //max length of a local part - 50
            "aschshshenansnfnesnrnenjklodasqwerthgjfmikmgkpgfdsaaaaaaaaaa@wp.pl, false"}) //length of a local part is over the limit
    public void isEmailFormatCorrect(String email, boolean expected) {

        Assert.assertEquals(expected, RegisterValidator.isEmailFormatCorrect(email));

    }


    @Test
    @Parameters({"abc, false", "ab!abd, false", "dsad!sdsdsdas, false", ",false", "dfadw!4dsa, true",
            "!4#@#$@$@!#$!@$, false", "!3fdjklpolfgjklomngtrhbgnmlkopf, false"}) //too long
    public void isPasswordFormatCorrect(String password, boolean expected) {

        Assert.assertEquals(expected, RegisterValidator.isPasswordFormatCorrect(password));

    }

    @Test
    public void registerValidatorMethodsGetNull() {
        Assert.assertFalse(RegisterValidator.isAgeCorrect(null));

        Assert.assertFalse(RegisterValidator.isNameFormatCorrect(null, "surname"));
        Assert.assertFalse(RegisterValidator.isNameFormatCorrect("firstname", null));
        Assert.assertFalse(RegisterValidator.isNameFormatCorrect(null, null));

        Assert.assertFalse(RegisterValidator.isEmailFormatCorrect(null));

        Assert.assertFalse(RegisterValidator.isPasswordFormatCorrect(null));

    }

}
