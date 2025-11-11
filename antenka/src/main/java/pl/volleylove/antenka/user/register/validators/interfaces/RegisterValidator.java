package pl.volleylove.antenka.user.register.validators.interfaces;

import pl.volleylove.antenka.user.register.RegisterRequest;

import java.time.LocalDate;
import java.util.List;

public interface RegisterValidator {
    boolean isAgeCorrect(LocalDate birthday);
    boolean isNameFormatCorrect(String name);
    boolean isEmailFormatCorrect(String email);
    boolean isPasswordFormatCorrect(String password);


}
