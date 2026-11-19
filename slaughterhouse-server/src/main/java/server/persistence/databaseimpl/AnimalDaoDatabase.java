package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import domain.Animal;
import server.persistence.NotFound;
import server.persistence.interfaces.AnimalDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  @Override public Optional<Animal> getAnimalByEarMark(String earmark)
  {
   return Optional.ofNullable(em.find(Animal.class, earmark));
  }

  @Override public List<Animal> getAllAnimals()
  {
    return em.createQuery("SELECT a FROM Animal a", Animal.class).getResultList();
  }

  @Override public List<Animal> getAnimalsByProductId(UUID productId)
  {
    return em.createQuery(
            "SELECT DISTINCT a FROM Animal a "
                + "JOIN AnimalPart ap ON ap.animal = a "
                + "JOIN ap.tray t "
                + "JOIN t.products p "
                + "WHERE p.id = :productId", Animal.class)
        .setParameter("productId", productId).getResultList();
  }

  @Override
  public void deleteAnimal(String earmark) {
    Animal animal = Optional.ofNullable(em.find(Animal.class, earmark))
        .orElseThrow(() -> new NotFound("Animal not found"));
    em.remove(animal);
  }
}
