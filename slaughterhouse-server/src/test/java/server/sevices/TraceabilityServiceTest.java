package server.sevices;

import domain.*;
import domain.Package;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.time.LocalDate;
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
    java.util.UUID productId = java.util.UUID.randomUUID();
    Animal animal = new Animal("DK-1", new Farm("TestFarm"), LocalDate.now(), 200, AnimalType.COW);
    when(animalDao.getAnimalsByProductId(productId)).thenReturn(List.of(animal));

    List<Animal> result = traceabilityService.traceAnimalsFromProduct(productId);

    assertEquals(1, result.size());
    assertTrue(result.contains(animal));
    verify(animalDao).getAnimalsByProductId(productId);
    verifyNoInteractions(partDao, productDao, trayDao);
  }

  @Test void traceAnimalsFromProduct_returnsEmptyListWhenNoAnimalsFound()
  {
    java.util.UUID productId = java.util.UUID.randomUUID();
    when(animalDao.getAnimalsByProductId(productId)).thenReturn(Collections.emptyList());

    List<Animal> result = traceabilityService.traceAnimalsFromProduct(productId);

    assertTrue(result.isEmpty());
    verify(animalDao).getAnimalsByProductId(productId);
  }

  @Test void traceProductFromAnimal_returnsProductsFromDao()
  {
    String animalId = "DK-1";
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
    String animalId = "DK-42";
    when(productDao.getProductsByAnimalId(animalId)).thenReturn(Collections.emptyList());

    List<Product> result = traceabilityService.traceProductFromAnimal(animalId);

    assertTrue(result.isEmpty());
    verify(productDao).getProductsByAnimalId(animalId);
  }
}
