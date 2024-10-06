package pl.volleylove.antenka.event.match.find;

import lombok.*;
import pl.volleylove.antenka.entity.Slot;
import pl.volleylove.antenka.enums.FindMatchInfo;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindMatchSlotsResponse {

    private FindMatchInfo info;
    private Set<Slot> slots;

}
