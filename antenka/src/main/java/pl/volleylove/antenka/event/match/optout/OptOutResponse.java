package pl.volleylove.antenka.event.match.optout;

import lombok.*;
import pl.volleylove.antenka.enums.OptOutInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptOutResponse {

    private OptOutInfo info;

}
