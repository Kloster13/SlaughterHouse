package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import server.domain.Tray;
import server.persistence.NotFound;
import server.persistence.interfaces.TrayDao;

import java.util.List;

@Repository
public class TrayDaoDatabase implements TrayDao
{
  private final EntityManager em;

  public TrayDaoDatabase(EntityManager em)
  {
    this.em = em;
  }

  @Override public void createTray(Tray tray)
  {
    em.persist(tray);
  }

  @Override public void update(Tray updatedTray)
  {
    em.merge(updatedTray);
  }

  @Override public Tray getTrayById(long id)
  {
    Tray toFind = em.find(Tray.class, id);
    if (toFind == null)
      throw new NotFound("Tray not found");
    return toFind;
  }

  @Override public List<Tray> getAllTrays()
  {
    return em.createQuery("SELECT t FROM Tray t", Tray.class).getResultList();
  }

  @Override public void deleteTray(long id)
  {
    Tray toRemove = getTrayById(id);
    if (toRemove == null)
    {
      throw new NotFound("Tray not found");
    }
    em.remove(toRemove);
  }
}
