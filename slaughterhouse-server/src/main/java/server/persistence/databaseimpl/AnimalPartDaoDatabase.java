package server.persistence.databaseimpl;

import domain.Tray;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import domain.AnimalPart;
import server.persistence.NotFound;
import server.persistence.interfaces.AnimalPartDao;

import java.util.List;
import java.util.UUID;

@Repository
public class AnimalPartDaoDatabase implements AnimalPartDao
{
  private final EntityManager em;

  public AnimalPartDaoDatabase(EntityManager em)
  {
    this.em = em;
  }

  @Override public void createAnimalPart(AnimalPart animalPart)
  {
    em.persist(animalPart);
  }

  @Override public void update(AnimalPart updatedAnimalPart)
  {
    em.merge(updatedAnimalPart);
  }

  @Override public boolean existsById(UUID id)
  {
    return em.find(AnimalPart.class, id) != null;
  }

  @Override public AnimalPart getAnimalPartById(UUID id)
  {
    AnimalPart toFind = em.find(AnimalPart.class, id);
    if (toFind == null)
      throw new NotFound("AnimalPart not found");
    return toFind;
  }

  @Override public List<AnimalPart> getAllAnimalParts()
  {
    return em.createQuery("SELECT a FROM AnimalPart a", AnimalPart.class).getResultList();
  }

  @Override public void deleteAnimalPart(UUID id)
  {
    AnimalPart toRemove = getAnimalPartById(id);
    if (toRemove == null)
    {
      throw new NotFound("AnimalPart not found");
    }
    em.remove(toRemove);
  }
}
