package pl.volleylove.antenka.event.match.find;

import lombok.Builder;
import lombok.Getter;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.enums.FindMatchInfo;

import java.util.Objects;
import java.util.Set;

@Getter
@Builder
public class FindMatchResponse {

    private FindMatchInfo findMatchInfo;
    private Set<Match> matches;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindMatchResponse that = (FindMatchResponse) o;
        return findMatchInfo == that.findMatchInfo && Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(findMatchInfo, matches);
    }
}
