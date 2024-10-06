package pl.volleylove.antenka.event.match.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class SignUpForMatchRequest {

    @NotNull
    private Long eventID;

    @NotNull
    @JsonProperty("slotNum")
    private int orderNum;

}
