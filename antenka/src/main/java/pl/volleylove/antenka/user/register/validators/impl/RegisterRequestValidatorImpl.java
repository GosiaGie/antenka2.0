package pl.volleylove.antenka.user.register.validators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.user.register.RegisterRequest;
import pl.volleylove.antenka.user.register.validators.interfaces.RegisterRequestValidator;
import pl.volleylove.antenka.user.register.validators.interfaces.RegisterValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class RegisterRequestValidatorImpl implements RegisterRequestValidator {

    private final RegisterValidator registerValidator;

    @Autowired
    public RegisterRequestValidatorImpl(RegisterValidator registerValidator) {
        this.registerValidator = registerValidator;
    }

    @Override
    public List<RegisterInfo> validateRequest(RegisterRequest request) {

        List<RegisterInfo> validateInfo = new ArrayList<>();

        if ((request == null) || Stream.of(request.getFirstName(), request.getLastName(),
                        request.getEmail(), request.getPassword(), request.getBirthday())
                .anyMatch(Objects::isNull)) {
            validateInfo.add(RegisterInfo.MISSING_INFO);
            return validateInfo;
        }

        if (!registerValidator.isNameFormatCorrect(request.getFirstName())) {
            validateInfo.add(RegisterInfo.INCORRECT_FIRST_NAME);
        }
        if (!registerValidator.isNameFormatCorrect(request.getLastName())) {
            validateInfo.add(RegisterInfo.INCORRECT_LAST_NAME);
        }
        if (!registerValidator.isEmailFormatCorrect(request.getEmail())) {
            validateInfo.add(RegisterInfo.INCORRECT_EMAIL);
        }
        if (!registerValidator.isPasswordFormatCorrect(request.getPassword())) {
            validateInfo.add(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ);
        }
        if (!registerValidator.isAgeCorrect(request.getBirthday())) {
            validateInfo.add(RegisterInfo.NOT_ALLOWED_AGE);
        }

        return validateInfo;

    }
}
