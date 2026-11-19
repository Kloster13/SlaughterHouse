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
import java.util.ArrayList;
import java.util.List;

@Service public class RegistrationService
{
  private final AnimalRepository animalRepository;
  private final FarmRepository farmRepository;

  public RegistrationService(AnimalRepository animalRepository, FarmRepository farmRepository)
  {
    this.animalRepository = animalRepository;
    this.farmRepository = farmRepository;
  }

  public Animal getAnimal(String earmark)
  {
    return animalRepository.findById(earmark).orElseThrow(() -> new NotFound("Animal Not found"));
  }

  public AnimalDto registerAnimal(RegisterRequest request)
  {
    Farm farm = farmRepository.findById(request.farmId()).orElseThrow(() -> new NotFound("Farm Not found"));
    Animal savedAnimal = animalRepository.save(
        new Animal(request.earmark(), farm, LocalDate.now(), request.weight(), request.animalType()));
    return new AnimalDto(savedAnimal.getEarmark(), farm.getName(), savedAnimal.getArrivalDate(),
        savedAnimal.getType(), savedAnimal.getWeight());
  }

  public List<AnimalDto> getAnimalsByFarmNameOrDate(String farmName,LocalDate date)
  {
    List<AnimalDto> toReturn = new ArrayList<>();

    record Filter(LocalDate date, String name) {}
    List<Animal> animals =  switch (new Filter(date, farmName)) {
      case Filter(LocalDate d, String n) when d != null && n != null -> animalRepository.findByFarmNameAndArrivalDate(n, d);
      case Filter(LocalDate d, String n) when d != null -> animalRepository.findByArrivalDate(d);
      case Filter(LocalDate d, String n) when n != null -> animalRepository.findByFarmName(n);
      default -> animalRepository.findAll();
    };
    for (Animal animal : animals)
    {
      toReturn.add(
          new AnimalDto(animal.getEarmark(), animal.getFarm().getName(), animal.getArrivalDate(), animal.getType(),
              animal.getWeight()));
    }
    return toReturn;
  }
}
