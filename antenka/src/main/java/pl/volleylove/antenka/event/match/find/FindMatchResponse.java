package pl.volleylove.antenka.event.match.find;

import lombok.Builder;
import lombok.Getter;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.enums.FindMatchInfo;

import java.util.Set;

@Getter
@Builder
public class FindMatchResponse {

    private FindMatchInfo findMatchInfo;
    private Set<Match> matches;

}
