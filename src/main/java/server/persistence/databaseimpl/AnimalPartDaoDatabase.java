package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import server.domain.AnimalPart;
import server.persistence.NotFound;
import server.persistence.interfaces.AnimalPartDao;

import java.util.List;

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

  @Override public AnimalPart getAnimalPartById(long id)
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

  @Override public void deleteAnimalPart(long id)
  {
    AnimalPart toRemove = getAnimalPartById(id);
    if (toRemove == null)
    {
      throw new NotFound("AnimalPart not found");
    }
    em.remove(toRemove);
  }
}
