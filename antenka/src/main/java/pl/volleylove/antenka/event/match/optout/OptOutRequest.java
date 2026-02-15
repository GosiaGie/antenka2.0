package pl.volleylove.antenka.event.match.optout;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.volleylove.antenka.enums.OptOutReason;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OptOutRequest {

    @NotNull
    private Long eventID;

    @NotNull
    @JsonProperty("slotNum")
    private int orderNum;

    @NotNull
    private OptOutReason optOutReason;
}
