package pl.volleylove.antenka.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

//TODO - training services
@Entity
@Table(name = "training_days")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_dayID", nullable = false)
    private Long trainingDayID;

    @ManyToOne
    @JoinColumn(name = "trainingID")
    @JsonIncludeProperties({"id"})
    private Training training;

    @Column(name = "time", columnDefinition = "varchar(8)")
    private LocalTime time;

    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Override
    public String toString() {
        return "TrainingDay{" +
                "trainingDayID=" + trainingDayID +
                ", training=" + training.getEventID() +
                ", time=" + time +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingDay that = (TrainingDay) o;
        return Objects.equals(trainingDayID, that.trainingDayID) && Objects.equals(training.getEventID(), that.training.getEventID()) && Objects.equals(time, that.time) && dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingDayID, training.getEventID(), time, dayOfWeek);
    }
}
