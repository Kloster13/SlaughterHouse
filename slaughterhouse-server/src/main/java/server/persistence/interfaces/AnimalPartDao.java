package server.persistence.interfaces;

import domain.AnimalPart;

import java.util.List;
import java.util.UUID;

public interface AnimalPartDao
{
  void createAnimalPart(AnimalPart animalPart);
  void update(AnimalPart updatedAnimalPart);
  boolean existsById(UUID id);
  AnimalPart getAnimalPartById(UUID id);
  List<AnimalPart> getAllAnimalParts();
  void deleteAnimalPart(UUID id);
}
