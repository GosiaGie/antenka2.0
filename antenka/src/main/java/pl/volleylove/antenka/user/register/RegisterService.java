package pl.volleylove.antenka.user.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.User;
import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.enums.Role;
import pl.volleylove.antenka.user.UserService;
import pl.volleylove.antenka.user.register.validators.interfaces.RegisterRequestValidator;

import java.util.List;

@Service
public class RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RegisterRequestValidator validator;

    @Autowired
    public RegisterService(UserService userService, PasswordEncoder passwordEncoder, RegisterRequestValidator validator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        List<RegisterInfo> validateInfo = validator.validateRequest(registerRequest);
        if (!validateInfo.isEmpty()) {
            return RegisterResponse.builder()
                    .email(registerRequest == null || registerRequest.getEmail() == null ? "" : registerRequest.getEmail())
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
}
