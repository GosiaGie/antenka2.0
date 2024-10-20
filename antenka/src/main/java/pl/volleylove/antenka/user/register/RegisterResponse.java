package pl.volleylove.antenka.user.register;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pl.volleylove.antenka.enums.RegisterInfo;

import java.util.List;
import java.util.Objects;

@Getter
@Builder
@ToString
public class RegisterResponse {

    private String email;
    private List<RegisterInfo> registerInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResponse that = (RegisterResponse) o;
        return Objects.equals(email, that.email) && Objects.equals(registerInfo, that.registerInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, registerInfo);
    }
}
