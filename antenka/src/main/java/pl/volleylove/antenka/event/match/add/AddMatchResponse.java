package pl.volleylove.antenka.event.match.add;

import lombok.*;
import pl.volleylove.antenka.entity.Match;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddMatchResponse {

    private List<String> addMatchInfo;
    private Match match;

}
