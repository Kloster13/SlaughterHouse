package server.sevices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.domain.*;
import server.domain.Package;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityServiceTest
{
  @Mock private AnimalDao animalDao;
  @Mock private AnimalPartDao partDao;
  @Mock private ProductDao productDao;
  @Mock private TrayDao trayDao;

  private TraceabilityService traceabilityService;

  @BeforeEach void setUp()
  {
    traceabilityService = new TraceabilityService(animalDao, partDao, productDao, trayDao);
  }

  @Test void traceAnimalsFromProduct_returnsAnimalsFromDao()
  {
    long productId = 1L;
    Animal animal = new Animal(new Farm("TestFarm"), 200, AnimalType.COW);
    when(animalDao.getAnimalsByProductId(productId)).thenReturn(List.of(animal));

    List<Animal> result = traceabilityService.traceAnimalsFromProduct(productId);

    assertEquals(1, result.size());
    assertTrue(result.contains(animal));
    verify(animalDao).getAnimalsByProductId(productId);
    verifyNoInteractions(partDao, productDao, trayDao);
  }

  @Test void traceAnimalsFromProduct_returnsEmptyListWhenNoAnimalsFound()
  {
    long productId = 42L;
    when(animalDao.getAnimalsByProductId(productId)).thenReturn(Collections.emptyList());

    List<Animal> result = traceabilityService.traceAnimalsFromProduct(productId);

    assertTrue(result.isEmpty());
    verify(animalDao).getAnimalsByProductId(productId);
  }

  @Test void traceProductFromAnimal_returnsProductsFromDao()
  {
    long animalId = 1L;
    Product product = new Package("PKG-0001", null, 12.5);
    when(productDao.getProductsByAnimalId(animalId)).thenReturn(List.of(product));

    List<Product> result = traceabilityService.traceProductFromAnimal(animalId);

    assertEquals(1, result.size());
    assertTrue(result.contains(product));
    verify(productDao).getProductsByAnimalId(animalId);
    verifyNoInteractions(animalDao, partDao, trayDao);
  }

  @Test void traceProductFromAnimal_returnsEmptyListWhenNoProductsFound()
  {
    long animalId = 42L;
    when(productDao.getProductsByAnimalId(animalId)).thenReturn(Collections.emptyList());

    List<Product> result = traceabilityService.traceProductFromAnimal(animalId);

    assertTrue(result.isEmpty());
    verify(productDao).getProductsByAnimalId(animalId);
  }
}
