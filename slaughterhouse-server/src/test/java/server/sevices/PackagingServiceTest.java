package server.sevices;

import domain.HalfAnimal;
import domain.Package;
import domain.Product;
import domain.Tray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Message;
import partingmessaging.ProductRegistrationMessage;
import partingmessaging.ProductType;
import server.persistence.NotFound;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PackagingServiceTest
{
  private ProductDao productDao;
  private TrayDao trayDao;
  private PackagingService service;
  private final UUID tray1 = UUID.randomUUID();
  private final UUID tray2 = UUID.randomUUID();

  @BeforeEach void setUp()
  {
    productDao = mock(ProductDao.class);
    trayDao = mock(TrayDao.class);
    service = new PackagingService(productDao, trayDao);
    when(trayDao.getTrayById(tray1)).thenReturn(new Tray(tray1, 100, 40.0));
    when(trayDao.getTrayById(tray2)).thenReturn(new Tray(tray2, 100, 25.5));
  }

  private ProductRegistrationMessage message(ProductType type, List<UUID> trayIds)
  {
    return new ProductRegistrationMessage(UUID.randomUUID(), type, "SERIAL-1", LocalDate.of(2026, 11, 19), trayIds);
  }

  private Product registerAndCapture(ProductRegistrationMessage message)
  {
    service.registerProduct(message);
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productDao).createProduct(captor.capture());
    return captor.getValue();
  }

  @Test void registerProduct_createsPackageWithSummedWeightAndTrays()
  {
    ProductRegistrationMessage message = message(ProductType.PACKAGE, List.of(tray1, tray2));

    Product product = registerAndCapture(message);

    assertInstanceOf(Package.class, product);
    assertEquals(message.productId(), product.getId());
    assertEquals("SERIAL-1", product.getSerial());
    assertEquals(LocalDate.of(2026, 11, 19), product.getPackagedDate());
    assertEquals(65.5, product.getWeight(), 0.0001);
    assertEquals(2, product.getTrays().size());
  }

  @Test void registerProduct_createsHalfAnimal()
  {
    Product product = registerAndCapture(message(ProductType.HALF_ANIMAL, List.of(tray1)));

    assertInstanceOf(HalfAnimal.class, product);
    assertEquals(40.0, product.getWeight(), 0.0001);
  }

  @Test void registerProduct_ignoresAlreadyRegisteredProduct()
  {
    ProductRegistrationMessage message = message(ProductType.PACKAGE, List.of(tray1));
    when(productDao.existsById(message.productId())).thenReturn(true);

    service.registerProduct(message);

    verify(productDao, never()).createProduct(any());
  }

  @Test void registerProduct_throwsNotFoundForUnknownTray()
  {
    UUID unknown = UUID.randomUUID();
    when(trayDao.getTrayById(unknown)).thenThrow(new NotFound("Tray not found"));

    assertThrows(NotFound.class,
        () -> service.registerProduct(message(ProductType.PACKAGE, List.of(tray1, unknown))));
    verify(productDao, never()).createProduct(any());
  }

  @Test void registerProduct_rejectsInvalidMessages()
  {
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(null));
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(message(null, List.of(tray1))));
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(message(ProductType.PACKAGE, List.of())));
    assertThrows(IllegalArgumentException.class,
        () -> service.registerProduct(message(ProductType.PACKAGE, Arrays.asList(tray1, null))));
    assertThrows(IllegalArgumentException.class,
        () -> service.registerProduct(message(ProductType.PACKAGE, List.of(tray1, tray1))));
    verify(productDao, never()).createProduct(any());
  }

  @Test void jackson2ConverterReadsMessageSentByPackaging()
  {
    String json = "{\"productId\":\"" + UUID.randomUUID() + "\",\"productType\":\"PACKAGE\",\"serial\":\"PKG-1\","
        + "\"packagedDate\":\"2026-11-19\",\"trayIds\":[\"" + tray1 + "\"]}";
    MessageProperties props = new MessageProperties();
    props.setContentType("application/json");
    props.setInferredArgumentType(ProductRegistrationMessage.class);

    Object result = new Jackson2JsonMessageConverter().fromMessage(new Message(json.getBytes(StandardCharsets.UTF_8), props));

    ProductRegistrationMessage message = assertInstanceOf(ProductRegistrationMessage.class, result);
    assertEquals(LocalDate.of(2026, 11, 19), message.packagedDate());
    assertEquals(List.of(tray1), message.trayIds());
  }
}
