package pl.volleylove.antenka.repository;

import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.User;

@Repository
public interface UserRepositoryCustom <T,S> {
    User findUserByEmail(String email);

}
