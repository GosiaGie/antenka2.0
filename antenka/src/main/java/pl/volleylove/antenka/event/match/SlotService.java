package pl.volleylove.antenka.event.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.entity.Slot;
import pl.volleylove.antenka.event.PlayerWanted;
import pl.volleylove.antenka.repository.SlotRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class SlotService {

    private SlotRepository slotRepository;

    @Autowired
    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public Set<Slot> saveSlotsInMatch(Long randomMatchID, List<PlayerWanted> players) {

        //1. Every PlayerWanted will get be in slot, with infos about match and orderNum
        //2. changing type from List to Set, for preventing duplicates
        //3. setting every slot its order number for this match - to better order
        //and to prevent replacing slots with the same requirements (Set can't have duplicates)
        //4. setting every slot its eventID

        int orderNum = 1;
        Match match = Match.builder()
                .eventID(randomMatchID)
                .build();


        Set<Slot> slots = new HashSet<>();
        Slot slot;

        for (PlayerWanted player : players) {
            slot = Slot.builder()
                    .event(match)
                    .playerWanted(player)
                    .orderNum(orderNum)
                    .build();
            slots.add(slot);

            orderNum++;
        }

        slotRepository.saveAll(slots);

        return slots;

    }

}





