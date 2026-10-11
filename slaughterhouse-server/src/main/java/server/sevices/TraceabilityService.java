package server.sevices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import domain.Animal;
import domain.Product;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.util.List;

@Service
public class TraceabilityService
{
  private final AnimalDao animalDao;
  private final AnimalPartDao partDao;
  private final ProductDao productDao;
  private final TrayDao trayDao;

  public TraceabilityService(AnimalDao animalDao, AnimalPartDao partDao, ProductDao productDao,
      TrayDao trayDao)
  {
    this.animalDao = animalDao;
    this.partDao = partDao;
    this.productDao = productDao;
    this.trayDao = trayDao;
  }

  @Transactional(readOnly = true)
  public List<Animal> traceAnimalsFromProduct(long productId)
  {
    return animalDao.getAnimalsByProductId(productId);
  }

  @Transactional(readOnly = true)
  public List<Product> traceProductFromAnimal(long animalId)
  {
    return productDao.getProductsByAnimalId(animalId);
  }
}
