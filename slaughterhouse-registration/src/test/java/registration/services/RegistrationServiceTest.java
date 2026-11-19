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
  private static final java.util.UUID FARM = java.util.UUID.randomUUID();
  @Mock private AnimalRepository animalRepository;
  @Mock private FarmRepository farmRepository;

  private RegistrationService service;

  @BeforeEach void setUp()
  {
    service = new RegistrationService(animalRepository, farmRepository);
  }

  private Animal animal(String id, Farm farm, LocalDate date, double weight, AnimalType type)
  {
    Animal animal = new Animal(id, farm, date, weight, type);
    animal.setEarmark(id);
    return animal;
  }

  // ---------- getAnimal ----------

  @Test void getAnimal_returnsAnimalWhenFound()
  {
    Animal animal = animal("DK-1", new Farm("Farm"), LocalDate.of(2026, 10, 12), 200, AnimalType.COW);
    when(animalRepository.findById("DK-1")).thenReturn(Optional.of(animal));

    assertSame(animal, service.getAnimal("DK-1"));
  }

  @Test void getAnimal_throwsNotFoundWhenMissing()
  {
    when(animalRepository.findById("DK-99")).thenReturn(Optional.empty());

    NotFound ex = assertThrows(NotFound.class, () -> service.getAnimal("DK-99"));
    assertEquals("Animal Not found", ex.getMessage());
  }

  // ---------- registerAnimal ----------

  @Test void registerAnimal_savesAnimalWithFarmAndTodayAndReturnsDto()
  {
    Farm farm = new Farm("Sunny Farm");
    when(farmRepository.findById(FARM)).thenReturn(Optional.of(farm));
    when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> {
      Animal saved = inv.getArgument(0);
      saved.setEarmark("DK-10");
      return saved;
    });

    AnimalDto dto = service.registerAnimal(new RegisterRequest("DK-10", FARM, AnimalType.PIG, 120.5));

    ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
    verify(animalRepository).save(captor.capture());
    Animal saved = captor.getValue();
    assertSame(farm, saved.getFarm());
    assertEquals(LocalDate.now(), saved.getArrivalDate());
    assertEquals(120.5, saved.getWeight());
    assertEquals(AnimalType.PIG, saved.getType());

    assertEquals("DK-10", dto.earmark());
    assertEquals("Sunny Farm", dto.farmName());
    assertEquals(LocalDate.now(), dto.arrivalDate());
    assertEquals(AnimalType.PIG, dto.animalType());
    assertEquals(120.5, dto.weight());
  }

  @Test void registerAnimal_throwsNotFoundAndDoesNotSaveWhenFarmMissing()
  {
    when(farmRepository.findById(FARM)).thenReturn(Optional.empty());

    NotFound ex = assertThrows(NotFound.class,
        () -> service.registerAnimal(new RegisterRequest("DK-10", FARM, AnimalType.COW, 300)));

    assertEquals("Farm Not found", ex.getMessage());
    verify(animalRepository, never()).save(any());
  }

  // ---------- getAnimalsByFarmNameOrDate ----------

  @Test void getAnimals_withFarmNameAndDate_usesCombinedQuery()
  {
    LocalDate date = LocalDate.of(2026, 10, 12);
    Animal animal = animal("DK-1", new Farm("Farm A"), date, 200, AnimalType.COW);
    when(animalRepository.findByFarmNameAndArrivalDate("Farm A", date)).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate("Farm A", date);

    assertEquals(1, result.size());
    assertEquals(new AnimalDto("DK-1", "Farm A", date, AnimalType.COW, 200), result.get(0));
    verify(animalRepository).findByFarmNameAndArrivalDate("Farm A", date);
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withOnlyDate_usesDateQuery()
  {
    LocalDate date = LocalDate.of(2026, 10, 12);
    Animal animal = animal("DK-2", new Farm("Farm B"), date, 90, AnimalType.PIG);
    when(animalRepository.findByArrivalDate(date)).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate(null, date);

    assertEquals(1, result.size());
    assertEquals("Farm B", result.get(0).farmName());
    verify(animalRepository).findByArrivalDate(date);
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withOnlyFarmName_usesFarmNameQuery()
  {
    Animal animal = animal("DK-3", new Farm("Farm C"), LocalDate.of(2026, 1, 1), 150, AnimalType.COW);
    when(animalRepository.findByFarmName("Farm C")).thenReturn(List.of(animal));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate("Farm C", null);

    assertEquals(1, result.size());
    assertEquals("DK-3", result.get(0).earmark());
    verify(animalRepository).findByFarmName("Farm C");
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_withNoFilters_returnsAll()
  {
    Farm farm = new Farm("Farm D");
    when(animalRepository.findAll()).thenReturn(List.of(
        animal("DK-1", farm, LocalDate.of(2026, 1, 1), 100, AnimalType.COW),
        animal("DK-2", farm, LocalDate.of(2026, 1, 2), 110, AnimalType.PIG)));

    List<AnimalDto> result = service.getAnimalsByFarmNameOrDate(null, null);

    assertEquals(2, result.size());
    assertEquals("DK-1", result.get(0).earmark());
    assertEquals("DK-2", result.get(1).earmark());
    verify(animalRepository).findAll();
    verifyNoMoreInteractions(animalRepository);
  }

  @Test void getAnimals_returnsEmptyListWhenNoMatches()
  {
    when(animalRepository.findByFarmName("Nobody")).thenReturn(List.of());

    assertTrue(service.getAnimalsByFarmNameOrDate("Nobody", null).isEmpty());
  }
}
