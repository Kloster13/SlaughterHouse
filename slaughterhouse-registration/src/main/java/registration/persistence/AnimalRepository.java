package registration.persistence;

import domain.Animal;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long>
{
  List<Animal> findByArrivalDate(LocalDate arrivalDate);
  List<Animal> findByFarmName(String farmName);
  List<Animal> findByFarmNameAndArrivalDate(String farmName, LocalDate arrivalDate);
}
