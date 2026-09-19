package server.persistence.interfaces;

import server.domain.AnimalPart;

import java.util.List;

public interface AnimalPartDao
{
  void createAnimalPart(AnimalPart AnimalPart);
  void update(AnimalPart updatedAnimalPart);
  AnimalPart getAnimalPartById(int id);
  List<AnimalPart> getAllAnimalParts();
  void deleteAnimalPart(int id);
}
