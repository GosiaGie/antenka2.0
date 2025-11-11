package pl.volleylove.antenka.user.register.validators.interfaces;

import pl.volleylove.antenka.enums.RegisterInfo;
import pl.volleylove.antenka.user.register.RegisterRequest;

import java.util.List;

public interface RegisterRequestValidator {

    List<RegisterInfo> validateRequest(RegisterRequest request);

}
