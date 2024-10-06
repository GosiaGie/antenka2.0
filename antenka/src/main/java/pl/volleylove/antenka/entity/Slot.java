package pl.volleylove.antenka.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.*;
 import pl.volleylove.antenka.event.PlayerWanted;

import java.util.Objects;

@Entity(name = "slot")
@Table(name = "slots")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slotID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eventID")
    @JsonIncludeProperties(value = {"eventID"})
    private Event event;

    //number unique for every slot in ONE event
    @Column(name = "order_num")
    private int orderNum;

    @Embedded
    private PlayerWanted playerWanted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_appliedID")
    @JsonIncludeProperties(value = {"playerProfileID"})
    private PlayerProfile playerApplied;


    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                ", event=" + event.getEventID() +
                ", orderNum=" + orderNum +
                ", playerWanted=" + playerWanted +
                ", playerApplied=" + playerApplied.getPlayerProfileID() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return orderNum == slot.orderNum && Objects.equals(id, slot.id) && Objects.equals(event.getEventID(), slot.event.getEventID()) && Objects.equals(playerWanted, slot.playerWanted) && Objects.equals(playerApplied.getPlayerProfileID(), slot.playerApplied.getPlayerProfileID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event.getEventID(), orderNum, playerWanted, playerApplied == null ? 0 : playerApplied.getPlayerProfileID());
    }
}
