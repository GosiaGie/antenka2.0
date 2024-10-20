package pl.volleylove.antenka.user.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.enums.Role;
import pl.volleylove.antenka.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class RegisterService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegisterService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        //checking if request or any field of it is null
        if (isNullOrHasNull(registerRequest)) {
            return RegisterResponse.builder()
                    .registerInfo(List.of(RegisterInfo.MISSING_INFO))
                    .build();
        }

        //validation data - if any data doesn't meet requirements, then return response
        List<RegisterInfo> validateInfo = validateData(registerRequest);
        if (!validateInfo.isEmpty()) {
            return RegisterResponse.builder()
                    .email(registerRequest.getEmail())
                    .registerInfo(validateInfo).build();
        }

        //checking if email is unique
        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
            return RegisterResponse.builder()
                    .email(registerRequest.getEmail())
                    .registerInfo(List.of(RegisterInfo.EMAIL_ALREADY_EXISTS))
                    .build();
        }

        //saving user's data
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthday(registerRequest.getBirthday())
                .role(Role.ROLE_USER)
                .build();

        userService.save(user);

        //building response
        return RegisterResponse.builder()
                .email(registerRequest.getEmail())
                .registerInfo(List.of(RegisterInfo.OK))
                .build();

    }


    private List<RegisterInfo> validateData(RegisterRequest request) {

        List<RegisterInfo> validateInfo = new ArrayList<>();

        if (!RegisterValidator.isNameFormatCorrect(request.getFirstName())) {
            validateInfo.add(RegisterInfo.INCORRECT_FIRST_NAME);
        }
        if (!RegisterValidator.isNameFormatCorrect(request.getLastName())) {
            validateInfo.add(RegisterInfo.INCORRECT_LAST_NAME);
        }
        if (!RegisterValidator.isEmailFormatCorrect(request.getEmail())) {
            validateInfo.add(RegisterInfo.INCORRECT_EMAIL);
        }
        if (!RegisterValidator.isPasswordFormatCorrect(request.getPassword())) {
            validateInfo.add(RegisterInfo.PASSWORD_DOES_NOT_MEET_REQ);
        }
        if (!RegisterValidator.isAgeCorrect(request.getBirthday())) {
            validateInfo.add(RegisterInfo.NOT_ALLOWED_AGE);
        }

        return validateInfo;
    }

    private boolean isNullOrHasNull(RegisterRequest request) {

        if (request == null) {
            return true;
        }

        return Stream.of(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getBirthday())
                    .anyMatch(Objects::isNull);
    }

}
