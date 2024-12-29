package pl.volleylove.antenka.event.match.signup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.entity.Slot;
import pl.volleylove.antenka.enums.SignUpInfo;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpForMatchResponse {

    private SignUpInfo info;
    @JsonIgnoreProperties({"slots", "signingUp", "signingUpEndReason", "active", "freeSlots"})
    private Match match;
    private Slot slot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignUpForMatchResponse that = (SignUpForMatchResponse) o;
        return info == that.info && Objects.equals(match, that.match) && Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, match, slot);
    }
}
