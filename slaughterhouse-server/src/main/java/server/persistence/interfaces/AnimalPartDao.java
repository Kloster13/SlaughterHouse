package server.persistence.interfaces;

import server.domain.Animal;
import server.domain.AnimalPart;
import server.domain.Tray;

import java.util.List;

public interface AnimalPartDao
{
  void createAnimalPart(AnimalPart animalPart);
  void update(AnimalPart updatedAnimalPart);
  AnimalPart getAnimalPartById(long id);
  List<AnimalPart> getAllAnimalParts();
  void deleteAnimalPart(long id);
}
