package server.persistence.interfaces;

import domain.Animal;

import java.util.List;

public interface AnimalDao
{
  void createAnimal(Animal animal);
  void update(Animal updatedAnimal);
  Animal getAnimalById(long id);
  List<Animal> getAllAnimals();
  List<Animal> getAnimalsByProductId(long id);
  void deleteAnimal(long id);
}
