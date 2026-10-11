package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.Order;
import server.domain.Animal;
import server.persistence.NotFound;
import server.persistence.interfaces.AnimalDao;

import java.util.List;

@Repository
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
      throw new NotFound("Animal not found");
    return toFind;
  }

  @Override public List<Animal> getAllAnimals()
  {
    return em.createQuery("SELECT a FROM Animal a", Animal.class).getResultList();
  }

  @Override public List<Animal> getAnimalsByProductId(long productId)
  {
    return em.createQuery(
            "SELECT DISTINCT a FROM Animal a "
                + "JOIN AnimalPart ap ON ap.animal = a "
                + "JOIN ap.tray t "
                + "JOIN t.products p "
                + "WHERE p.id = :productId", Animal.class)
        .setParameter("productId", productId).getResultList();
  }

  @Override public void deleteAnimal(long id)
  {
    Animal toRemove = getAnimalById(id);
    if (toRemove == null)
    {
      throw new NotFound("Animal not found");
    }
    em.remove(toRemove);
  }
}
