package pl.volleylove.antenka.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.PlayerProfile;

@Repository
public interface PlayerProfileRepository extends CrudRepository<PlayerProfile, Long>,
        PlayerProfileRepositoryCustom<PlayerProfile, Long> {
}
