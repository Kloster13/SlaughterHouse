package registration;

import domain.Animal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import registration.dto.AnimalDto;
import registration.dto.AnimalsFromDateRequest;
import registration.dto.RegisterRequest;
import registration.persistence.AnimalRepository;
import registration.persistence.FarmRepository;
import registration.services.RegistrationService;

import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/animals") public class RegistrationController
{
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService)
  {
    this.registrationService = registrationService;
  }

  @GetMapping("/{id}") public ResponseEntity<Animal> getAnimal(@PathVariable long id)
  {
    return ResponseEntity.status(HttpStatus.OK).body(registrationService.getAnimal(id));
  }

  @GetMapping public ResponseEntity<List<AnimalDto>> getAnimals(@RequestParam(required = false) String farmName, @RequestParam(required = false) LocalDate date)
  {
    return ResponseEntity.ok(registrationService.getAnimalsByFarmNameOrDate(farmName,date));
  }

  @PostMapping public ResponseEntity<AnimalDto> registerAnimal(@RequestBody RegisterRequest request)
  {
    return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerAnimal(request));
  }
}
