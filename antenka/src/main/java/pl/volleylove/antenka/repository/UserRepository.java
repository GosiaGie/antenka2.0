package pl.volleylove.antenka.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom<User, Long> {

}
