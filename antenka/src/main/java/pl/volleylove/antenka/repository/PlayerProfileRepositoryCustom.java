package pl.volleylove.antenka.repository;

import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.PlayerProfile;
import pl.volleylove.antenka.entity.User;

import java.util.Optional;

@Repository
public interface PlayerProfileRepositoryCustom <T,S> {
    Optional<PlayerProfile> findByUser(User user);

}
