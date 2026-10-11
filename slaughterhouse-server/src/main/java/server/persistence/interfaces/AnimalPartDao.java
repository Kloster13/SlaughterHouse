package server.persistence.interfaces;

import domain.AnimalPart;

import java.util.List;

public interface AnimalPartDao
{
  void createAnimalPart(AnimalPart animalPart);
  void update(AnimalPart updatedAnimalPart);
  AnimalPart getAnimalPartById(long id);
  List<AnimalPart> getAllAnimalParts();
  void deleteAnimalPart(long id);
}
