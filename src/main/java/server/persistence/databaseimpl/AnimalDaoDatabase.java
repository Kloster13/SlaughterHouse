package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import server.domain.Animal;
import server.persistence.interfaces.AnimalDao;

import java.util.List;

public class AnimalDaoDatabase implements AnimalDao
{
  private final EntityManager em;

  public AnimalDaoDatabase(EntityManager em)
  {
    this.em = em;
  }

  @Override public void createAnimal(Animal animal)
  {
    em.persist(animal);
  }

  @Override public void update(Animal updatedAnimal)
  {
    em.merge(updatedAnimal);
  }

  @Override public Animal getAnimalById(long id)
  {
    Animal toFind = em.find(Animal.class, id);
    if (toFind == null)
      throw new EntityNotFoundException("Animal not found");
    return toFind;
  }

  @Override public List<Animal> getAllAnimals()
  {
    return em.createQuery("SELECT a FROM Animal a", Animal.class).getResultList();
  }

  @Override public void deleteAnimal(long id)
  {
    Animal toRemove = em.find(Animal.class, id);
    if (toRemove == null)
    {
      throw new EntityNotFoundException("Animal not found");
    }
    em.remove(toRemove);
  }
}
