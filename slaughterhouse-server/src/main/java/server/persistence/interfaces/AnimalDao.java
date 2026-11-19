package server.persistence.interfaces;

import domain.Animal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalDao
{
  void createAnimal(Animal animal);
  void update(Animal updatedAnimal);
  Optional<Animal> getAnimalByEarMark(String earmark);
  List<Animal> getAllAnimals();
  List<Animal> getAnimalsByProductId(UUID id);
  void deleteAnimal(String earmark);
}
