package pl.volleylove.antenka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Set;
//TODO - training services
@Table(name = "trainings")
@Entity(name = "training" )
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder
public class Training extends Event {

    @OneToMany(mappedBy = "training", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<TrainingDay> trainingDays;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Training training = (Training) o;
        return Objects.equals(trainingDays, training.trainingDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), trainingDays);
    }
}

