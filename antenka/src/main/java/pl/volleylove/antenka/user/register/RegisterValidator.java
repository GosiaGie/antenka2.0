package pl.volleylove.antenka.user.register;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterValidator {

    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 150;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MIN_PASS_LENGTH = 8;
    private static final int MAX_PASS_LENGTH = 30;

    private RegisterValidator() {

    }

    public static boolean isAgeCorrect(LocalDate birthday) {

        if(birthday == null) {
            return false;
        }

        LocalDate lastDateAllowed = LocalDate.now().minusYears(MIN_AGE);
        LocalDate firstDateAllowed = LocalDate.now().minusYears(MAX_AGE);

        return birthday.isEqual(firstDateAllowed) || birthday.isEqual(lastDateAllowed) || (birthday.isBefore(lastDateAllowed) && birthday.isAfter(firstDateAllowed));
    }

    public static boolean isNameFormatCorrect(String firstName, String lastName) {

        if(firstName == null || lastName ==null) {
            return false;
        }

        return firstName.chars().allMatch(Character::isLetter) && lastName.chars().allMatch(Character::isLetter)
                && firstName.length() >= MIN_NAME_LENGTH && firstName.length() <= MAX_NAME_LENGTH
                && lastName.length() >= MIN_NAME_LENGTH && lastName.length() <= MAX_NAME_LENGTH;

    }


    public static boolean isEmailFormatCorrect(String email) {

        if(email == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[\\w-.]+@([\\w-])+[.][\\w-]{2,4}$");
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }


    public static boolean isPasswordFormatCorrect(String password) {

        Pattern letters = Pattern.compile("[a-zA-Z]");
        Pattern digit = Pattern.compile("\\d");
        Pattern special = Pattern.compile("[a-zA-Z]");

        Matcher hasLetter = letters.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        Matcher hasSpecial = special.matcher(password);

        return password.length() >= MIN_PASS_LENGTH && password.length() <= MAX_PASS_LENGTH
                && hasLetter.find() && hasDigit.find() && hasSpecial.find();

    }

}


