package pl.volleylove.antenka.user.register;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@Builder
public class RegisterRequest {

    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private LocalDate birthday;

}
