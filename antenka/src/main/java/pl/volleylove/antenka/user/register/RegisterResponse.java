package pl.volleylove.antenka.user.register;

import lombok.Builder;
import lombok.Getter;
import pl.volleylove.antenka.enums.RegisterInfo;

import java.util.List;

@Getter
@Builder
public class RegisterResponse {

    private String email;
    private List<RegisterInfo> registerInfo;

}
