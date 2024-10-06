package pl.volleylove.antenka.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.volleylove.antenka.enums.Gender;
import pl.volleylove.antenka.enums.Level;
import pl.volleylove.antenka.enums.Position;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "player_profiles")
public class PlayerProfile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "playerID")
    private Long playerProfileID;

    @OneToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    private User user;

    @ElementCollection(targetClass = Position.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "player_positions", joinColumns = @JoinColumn(name = "playerID"))
    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private Set<Position> positions;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "benefit_card_number")
    private String benefitCardNumber;

    @OneToMany(mappedBy = "playerApplied", fetch = FetchType.EAGER)
    private Set<Slot> apps;

    @Transient
    private boolean isSignedUpForActiveEvent;

    @Transient
    private int age;

    @Transient
    private boolean activeBenefit;

    @Override
    public String toString() {
        return "PlayerProfile{" +
                "playerProfileID=" + playerProfileID +
                ", user=" + user.getUserID() +
                ", positions=" + positions +
                ", level=" + level +
                ", gender=" + gender +
                ", benefitCardNumber='" + benefitCardNumber + '\'' +
                ", apps=" + apps.size() +
                ", isSignedUpForActiveEvent=" + isSignedUpForActiveEvent +
                ", age=" + age +
                ", activeBenefit=" + activeBenefit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerProfile that = (PlayerProfile) o;
        return isSignedUpForActiveEvent == that.isSignedUpForActiveEvent && age == that.age && activeBenefit == that.activeBenefit && Objects.equals(playerProfileID, that.playerProfileID) && Objects.equals(user.getUserID(), that.user.getUserID()) && Objects.equals(positions, that.positions) && level == that.level && gender == that.gender && Objects.equals(benefitCardNumber, that.benefitCardNumber) && Objects.equals(apps, that.apps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerProfileID, user.getUserID(), positions, level, gender, benefitCardNumber, apps, isSignedUpForActiveEvent, age, activeBenefit);
    }
}
