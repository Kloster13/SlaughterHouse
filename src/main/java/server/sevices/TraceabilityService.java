package server.sevices;

import jakarta.persistence.EntityManager;
import server.domain.Animal;
import server.domain.Tray;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TraceabilityService
{
  private final AnimalDao animalDao;
  private final AnimalPartDao partDao;
  private final ProductDao productDao;
  private final TrayDao trayDao;
  private final EntityManager em;

  public TraceabilityService(AnimalDao animalDao, AnimalPartDao partDao, ProductDao productDao,
      TrayDao trayDao, EntityManager em)
  {
    this.animalDao = animalDao;
    this.partDao = partDao;
    this.productDao = productDao;
    this.trayDao = trayDao;
    this.em = em;
  }

  public List<Animal> traceFromProductToAnimals(long productId)
  {
    List<Animal> animalsToReturn = new ArrayList<>();
    Set<Tray> trays = productDao.getProductById(productId).getTrays();

    return animalsToReturn;
  }

}
