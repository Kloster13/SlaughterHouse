package registration.persistence;

import domain.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long>
{
  Optional<Farm> findByName(String name);
}
