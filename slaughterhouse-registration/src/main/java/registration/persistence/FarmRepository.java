package registration.persistence;

import domain.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, UUID>
{
  Optional<Farm> findByName(String name);
}
