package pl.volleylove.antenka.event.match.add;

import lombok.*;
import pl.volleylove.antenka.entity.Match;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddMatchResponse {

    private List<String> addMatchInfo;
    private Match match;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddMatchResponse response = (AddMatchResponse) o;
        return Objects.equals(addMatchInfo, response.addMatchInfo) && Objects.equals(match, response.match);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addMatchInfo, match);
    }
}
