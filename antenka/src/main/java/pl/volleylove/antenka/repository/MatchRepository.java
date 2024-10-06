package pl.volleylove.antenka.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long>,
        MatchRepositoryCustom<Match, Long> {

}
