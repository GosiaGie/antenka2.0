package pl.volleylove.antenka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "match")
@Table(name = "matches")
@OnDelete(action = OnDeleteAction.CASCADE)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Match extends Event {

    @Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;


    //every 24 h event in DB closes past matches  - this method is useful when event didn't close a past match yet
    @Override
    public boolean isActive() {
        if (super.isActive()) {
            return dateTime.isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Match match = (Match) o;
        return Objects.equals(dateTime, match.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateTime);
    }

    @Override
    public String toString() {
        return super.toString() + "Match{" +
                "dateTime=" + dateTime +
                '}';
    }
}
