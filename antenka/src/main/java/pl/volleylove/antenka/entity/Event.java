package pl.volleylove.antenka.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pl.volleylove.antenka.enums.SigningUpEndReason;
import pl.volleylove.antenka.event.Price;

import java.util.Objects;
import java.util.Set;

//upper class for every match and training
@ToString
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "events")
@Entity(name = "event")
public abstract class Event {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventID")
    private Long eventID;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.REMOVE)
    private Set<Slot> slots;

    @ManyToOne
    @JoinColumn(name = "organizerID")
    @JsonBackReference
    private User organizer;

    @Getter
    @Column(name = "name")
    private String name;

    @Embedded
    private Price price;

    @JoinColumn(name = "addressID")
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Column(name = "freeSlots")
    private int freeSlots;

    //number of wanted players
    @Column(name = "players_num")
    private int playersNum;

    //true means that event is not in the past, and it's not closed by organizer
    @Column(name = "is_active")
    private boolean isActive;

    //true means that event is not in the past, is not closed by organizer and has free slots left
    @Column(name = "is_signing_up")
    private boolean isSigningUp;

    @Column(name = "signing_up_end_reason")
    @Enumerated(EnumType.STRING)
    private SigningUpEndReason signingUpEndReason;

    public boolean isSigningUp() {
        if (!isActive()) {
            return false;
        } else {
            return getSlots().stream().anyMatch(s -> s.getPlayerApplied() == null);
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return freeSlots == event.freeSlots && isActive == event.isActive && isSigningUp == event.isSigningUp && Objects.equals(eventID, event.eventID) && Objects.equals(slots, event.slots) && Objects.equals(organizer, event.organizer) && Objects.equals(name, event.name) && Objects.equals(price, event.price) && Objects.equals(address, event.address) && signingUpEndReason == event.signingUpEndReason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID, slots, organizer, name, price, address, freeSlots, isActive, isSigningUp, signingUpEndReason);
    }
}
