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
    public void isAgeCorrectTestBirthdayIsNull() {

        Assert.assertFalse(RegisterValidator.isAgeCorrect(null));
    }




}
