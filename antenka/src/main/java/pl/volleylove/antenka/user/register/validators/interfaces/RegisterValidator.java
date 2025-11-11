package pl.volleylove.antenka.user.register.validators.interfaces;

import java.time.LocalDate;

public interface RegisterValidator {
    boolean isAgeCorrect(LocalDate birthday);
    boolean isNameFormatCorrect(String name);
    boolean isEmailFormatCorrect(String email);
    boolean isPasswordFormatCorrect(String password);


}
