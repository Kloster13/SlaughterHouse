package org.example.slaughterhousepackaging;

import org.example.slaughterhousepackaging.dtos.ProductRequest;
import org.example.slaughterhousepackaging.dtos.ProductResponse;
import org.springframework.stereotype.Service;
import partingmessaging.ProductRegistrationMessage;
import partingmessaging.ProductType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service public class PackagingService
{
  private final ProductMessageSender sender;

  public PackagingService(ProductMessageSender sender)
  {
    this.sender = sender;
  }

  public ProductResponse registerProduct(ProductRequest request)
  {
    validate(request);

    UUID productId = UUID.randomUUID();
    String serial = serialFor(request.productType(), productId);
    List<UUID> trayIds = List.copyOf(request.trayIds());

    sender.sendProduct(
        new ProductRegistrationMessage(productId, request.productType(), serial, LocalDate.now(), trayIds));
    return new ProductResponse(productId, serial, request.productType(), trayIds.size());
  }

  static String serialFor(ProductType type, UUID productId)
  {
    String prefix = type == ProductType.PACKAGE ? "PKG-" : "HALF-";
    return prefix + productId.toString().substring(0, 8).toUpperCase();
  }

  private static void validate(ProductRequest request)
  {
    if (request == null)
      throw new IllegalArgumentException("Request body is required");
    if (request.productType() == null)
      throw new IllegalArgumentException("productType is required (PACKAGE or HALF_ANIMAL)");
    if (request.trayIds() == null || request.trayIds().isEmpty())
      throw new IllegalArgumentException("A product must reference at least one tray");

    Set<UUID> seen = new HashSet<>();
    int index = 0;
    for (UUID trayId : request.trayIds())
    {
      String where = "trayIds[" + index++ + "]";
      if (trayId == null)
        throw new IllegalArgumentException(where + " must not be null");
      if (!seen.add(trayId))
        throw new IllegalArgumentException(where + " is a duplicate: " + trayId);
    }
  }
}
