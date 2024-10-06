package pl.volleylove.antenka.event.match.signup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.entity.Slot;
import pl.volleylove.antenka.enums.SignUpInfo;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForMatchResponse {

    private SignUpInfo info;
    @JsonIgnoreProperties({"slots", "signingUp", "signingUpEndReason", "active", "freeSlots"})
    private Match match;
    private Slot slot;

}
