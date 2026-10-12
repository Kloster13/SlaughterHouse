package registration.services;

import domain.Animal;
import domain.AnimalType;
import domain.Farm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import registration.dto.AnimalDto;
import registration.dto.RegisterRequest;
import registration.persistence.AnimalRepository;
import registration.persistence.FarmRepository;
import registration.persistence.NotFound;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest
{
  @Mock private AnimalRepository animalRepository;
  @Mock private FarmRepository farmRepository;

  private RegistrationService service;

  @BeforeEach void setUp()
  {
    service = new RegistrationService(animalRepository, farmRepository);
  }

  private Animal animal(long id, Farm farm, LocalDate date, double weight, AnimalType type)
  {
    Animal animal = new Animal(farm, date, weight, type);
    animal.setId(id);
    return animal;
  }

  // ---------- getAnimal ----------

  @Test void getAnimal_returnsAnimalWhenFound()
  {
    Animal animal = animal(1L, new Farm("Farm"), LocalDate.of(2026, 10, 12), 200, AnimalType.COW);
    when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

    assertSame(animal, service.getAnimal(1L));
  }

  @Test void getAnimal_throwsNotFoundWhenMissing()
  {
    when(animalRepository.findById(99L)).thenReturn(Optional.empty());

    NotFound ex = assertThrows(NotFound.class, () -> service.getAnimal(99L));
    assertEquals("Animal Not found", ex.getMessage());
  }

  // ---------- registerAnimal ----------

  @Test void registerAnimal_savesAnimalWithFarmAndTodayAndReturnsDto()
  {
    Farm farm = new Farm("Sunny Farm");
    when(farmRepository.findById(5L)).thenReturn(Optional.of(farm));
    when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> {
      Animal saved = inv.getArgument(0);
      saved.setId(10L);
      return saved;
    });

    AnimalDto dto = service.registerAnimal(new RegisterRequest(5L, AnimalType.PIG, 120.5));

    ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
    verify(animalRepository).save(captor.capture());
    Animal saved = captor.getValue();
    assertSame(farm, saved.getFarm());
    assertEquals(LocalDate.now(), saved.getArrivalDate());
    assertEquals(120.5, saved.getWeight());
    assertEquals(AnimalType.PIG, saved.getType());

    assertEquals(10L, dto.id());
    assertEquals("Sunny Farm", dto.farmName());
    assertEquals(LocalDate.now(), dto.arrivalDate());
    assertEquals(AnimalType.PIG, dto.animalType());
    assertEquals(120.5, dto.weight());
  }

  @Test void registerAnimal_throwsNotFoundAndDoesNotSaveWhenFarmMissing()
  {
    when(farmRepository.findById(5L)).thenReturn(Optional.empty());

    NotFound ex = assertThrows(NotFound.class,
        () -> service.registerAnimal(new RegisterRequest(5L, AnimalType.COW, 300)));

    assertEquals("Farm Not found", ex.getMessage());
    verify(animalRepository, never()).save(any());
  }

  // ---------- getAnimalsByFarmNameOrDate ----------

  @Test void getAnimals_withFarmNameAndDate_usesCombinedQuery()
  {
    LocalDate date = LocalDate.of(2026, 10, 12);
    Animal animal = animal(1L, new Farm("Farm A"), date, 200, AnimalType.COW);
    when(animalRepository.findByFarmNameAndArrivalDate("Farm A", date)).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate("Farm A", date);

    assertEquals(1, result.size());
    assertEquals(new AnimalDto(1L, "Farm A", date, AnimalType.COW, 200), result.get(0));
    verify(animalRepository).findByFarmNameAndArrivalDate("Farm A", date);
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withOnlyDate_usesDateQuery()
  {
    LocalDate date = LocalDate.of(2026, 10, 12);
    Animal animal = animal(2L, new Farm("Farm B"), date, 90, AnimalType.PIG);
    when(animalRepository.findByArrivalDate(date)).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate(null, date);

    assertEquals(1, result.size());
    assertEquals("Farm B", result.get(0).farmName());
    verify(animalRepository).findByArrivalDate(date);
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withOnlyFarmName_usesFarmNameQuery()
  {
    Animal animal = animal(3L, new Farm("Farm C"), LocalDate.of(2026, 1, 1), 150, AnimalType.COW);
    when(animalRepository.findByFarmName("Farm C")).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate("Farm C", null);

    assertEquals(1, result.size());
    assertEquals(3L, result.get(0).id());
    verify(animalRepository).findByFarmName("Farm C");
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withNoFilters_returnsAll()
  {
    Farm farm = new Farm("Farm D");
    when(animalRepository.findAll()).thenReturn(List.of(
        animal(1L, farm, LocalDate.of(2026, 1, 1), 100, AnimalType.COW),
        animal(2L, farm, LocalDate.of(2026, 1, 2), 110, AnimalType.PIG)));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate(null, null);

    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).id());
    assertEquals(2L, result.get(1).id());
    verify(animalRepository).findAll();
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_returnsEmptyListWhenNoMatches()
  {
    when(animalRepository.findByFarmName("Nobody")).thenReturn(List.of());

    assertTrue(service.getAnimalsByFarmNameOrDate("Nobody", null).isEmpty());
  }
}
