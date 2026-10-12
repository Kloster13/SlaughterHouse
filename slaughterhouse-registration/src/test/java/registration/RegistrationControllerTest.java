package registration;

import domain.Animal;
import domain.AnimalType;
import domain.Farm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import registration.dto.AnimalDto;
import registration.dto.RegisterRequest;
import registration.services.RegistrationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest
{
  @Mock private RegistrationService registrationService;

  private RegistrationController controller;

  @BeforeEach void setUp()
  {
    controller = new RegistrationController(registrationService);
  }

  @Test void getAnimal_returns200WithAnimal()
  {
    Animal animal = new Animal(new Farm("Farm"), LocalDate.now(), 100, AnimalType.COW);
    when(registrationService.getAnimal(1L)).thenReturn(animal);

    ResponseEntity<Animal> response = controller.getAnimal(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(animal, response.getBody());
  }

  @Test void getAnimals_passesFiltersToServiceAndReturns200()
  {
    LocalDate date = LocalDate.of(2026, 10, 12);
    List<AnimalDto> dtos = List.of(new AnimalDto(1L, "Farm", date, AnimalType.PIG, 80));
    when(registrationService.getAnimalsByFarmNameOrDate("Farm", date)).thenReturn(dtos);

    ResponseEntity<List<AnimalDto>> response = controller.getAnimals("Farm", date);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dtos, response.getBody());
    verify(registrationService).getAnimalsByFarmNameOrDate("Farm", date);
  }

  @Test void registerAnimal_returns201WithCreatedDto()
  {
    RegisterRequest request = new RegisterRequest(1L, AnimalType.COW, 250);
    AnimalDto dto = new AnimalDto(7L, "Farm", LocalDate.now(), AnimalType.COW, 250);
    when(registrationService.registerAnimal(request)).thenReturn(dto);

    ResponseEntity<AnimalDto> response = controller.registerAnimal(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto, response.getBody());
  }
}
