package pl.volleylove.antenka.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleylove.antenka.entity.Slot;

@Repository
public interface SlotRepository extends CrudRepository<Slot, Long> {

}
