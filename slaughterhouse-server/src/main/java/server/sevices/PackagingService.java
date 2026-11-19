package server.sevices;

import domain.HalfAnimal;
import domain.Package;
import domain.Product;
import domain.Tray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partingmessaging.ProductRegistrationMessage;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service public class PackagingService
{
  private static final Logger log = LoggerFactory.getLogger(PackagingService.class);
  private final ProductDao productDao;
  private final TrayDao trayDao;

  public PackagingService(ProductDao productDao, TrayDao trayDao)
  {
    this.productDao = productDao;
    this.trayDao = trayDao;
  }

  @Transactional public void registerProduct(ProductRegistrationMessage message)
  {
    validate(message);

    if (productDao.existsById(message.productId()))
    {
      log.warn("Product {} already registered, ignoring message", message.productId());
      return;
    }

    Set<Tray> trays = new HashSet<>();
    double weight = 0;
    for (UUID trayId : message.trayIds())
    {
      Tray tray = trayDao.getTrayById(trayId); // throws NotFound for unknown trays
      trays.add(tray);
      weight += tray.getWeight();
    }

    Product product = switch (message.productType())
    {
      case PACKAGE -> new Package(message.productId(), message.serial(), message.packagedDate(), weight);
      case HALF_ANIMAL -> new HalfAnimal(message.productId(), message.serial(), message.packagedDate(), weight);
    };
    product.setTrays(trays);
    productDao.createProduct(product);
  }

  private static void validate(ProductRegistrationMessage message)
  {
    if (message == null || message.productId() == null)
      throw new IllegalArgumentException("Product registration must have a productId");
    if (message.productType() == null)
      throw new IllegalArgumentException("Product " + message.productId() + " has no productType");
    if (message.serial() == null || message.serial().isBlank())
      throw new IllegalArgumentException("Product " + message.productId() + " has no serial");
    if (message.packagedDate() == null)
      throw new IllegalArgumentException("Product " + message.productId() + " has no packagedDate");
    if (message.trayIds() == null || message.trayIds().isEmpty())
      throw new IllegalArgumentException("Product " + message.productId() + " references no trays");

    Set<UUID> seen = new HashSet<>();
    for (UUID trayId : message.trayIds())
    {
      if (trayId == null)
        throw new IllegalArgumentException("Product " + message.productId() + " references a null tray id");
      if (!seen.add(trayId))
        throw new IllegalArgumentException("Product " + message.productId() + " references tray " + trayId + " twice");
    }
  }
}
