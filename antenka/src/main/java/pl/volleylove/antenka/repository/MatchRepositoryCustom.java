package pl.volleylove.antenka.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.Match;
import pl.volleylove.antenka.enums.Gender;
import pl.volleylove.antenka.enums.Level;
import pl.volleylove.antenka.enums.Position;

import java.math.BigDecimal;
import java.util.Set;

@Repository
public interface MatchRepositoryCustom<T, S> {

    @Query("SELECT m FROM match m " +
            "INNER JOIN m.slots slot " +
            "WHERE slot.playerWanted.gender = :gender " +
            "AND slot.playerWanted.level = :level " +
            "AND slot.playerWanted.ageRange.ageMin <= :age AND slot.playerWanted.ageRange.ageMax >= :age " +
            "AND slot.playerWanted.position IN :positions " +
            "AND m.price.benefitPrice <= :maxPrice " +
            "AND m.isActive = TRUE " +
            "AND m.isSigningUp = TRUE " +
            "AND slot.playerApplied IS NULL")
    Set<Match> findByPlayerWantedBenefit(Gender gender, Level level, int age, Set<Position> positions, BigDecimal maxPrice);


    @Query("SELECT m FROM match m " +
            "INNER JOIN m.slots slot " +
            "WHERE slot.playerWanted.gender = :gender " +
            "AND slot.playerWanted.level = :level " +
            "AND slot.playerWanted.ageRange.ageMin <= :age AND slot.playerWanted.ageRange.ageMax >= :age " +
            "AND slot.playerWanted.position IN :positions " +
            "AND m.price.regularPrice <= :maxPrice " +
            "AND m.isActive = TRUE " +
            "AND m.isSigningUp = TRUE " +
            "AND slot.playerApplied IS NULL")
    Set<Match> findByPlayerWantedNoBenefit(Gender gender, Level level, int age, Set<Position> positions, BigDecimal maxPrice);



}
