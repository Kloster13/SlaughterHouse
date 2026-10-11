package registration.services;

import domain.Animal;
import domain.Farm;
import org.springframework.stereotype.Service;
import registration.dto.AnimalDto;
import registration.dto.RegisterRequest;
import registration.persistence.AnimalRepository;
import registration.persistence.FarmRepository;
import registration.persistence.NotFound;

import java.time.LocalDate;

@Service public class RegistrationService
{
  private final AnimalRepository animalRepository;
  private final FarmRepository farmRepository;

  public RegistrationService(AnimalRepository animalRepository, FarmRepository farmRepository)
  {
    this.animalRepository = animalRepository;
    this.farmRepository = farmRepository;
  }

  public Animal getAnimal(long id)
  {
    return animalRepository.findById(id).orElseThrow(() -> new NotFound("Animal Not found"));
  }

  public AnimalDto registerAnimal(RegisterRequest request)
  {
    Farm farm = farmRepository.findById(request.farmId()).orElseThrow(() -> new NotFound("Farm Not found"));
    Animal savedAnimal = animalRepository.save(
        new Animal(farm, LocalDate.now(), request.weight(), request.animalType()));
    return new AnimalDto(savedAnimal.getId(), farm.getName(), savedAnimal.getArrivalDate(),
        savedAnimal.getType(), savedAnimal.getWeight());
  }
}
