package org.example.slaughterhouseparting;

import org.example.slaughterhouseparting.dtos.PartRequest;
import org.example.slaughterhouseparting.dtos.TrayRequest;
import org.example.slaughterhouseparting.dtos.TrayResponse;
import org.springframework.stereotype.Service;
import partingmessaging.AnimalPartRegistration;
import partingmessaging.TrayRegistrationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service public class PartingService
{
  private static final double WEIGHT_TOLERANCE = 0.001;
  private final TrayMessageSender traySender;

  public PartingService(TrayMessageSender traySender)
  {
    this.traySender = traySender;
  }

  public TrayResponse registerTrayWithParts(TrayRequest request)
  {
    double totalWeight = validate(request);

    List<AnimalPartRegistration> parts = new ArrayList<>();
    for (PartRequest part : request.parts())
    {
      parts.add(new AnimalPartRegistration(UUID.randomUUID(), part.animalEarmark().trim(), part.weight(),
          part.partType().trim()));
    }

    UUID trayId = UUID.randomUUID();
    traySender.sendTray(new TrayRegistrationMessage(trayId, request.maxWeight(), totalWeight, parts));
    return new TrayResponse(trayId, totalWeight, parts.size());
  }

  private static double validate(TrayRequest request)
  {
    if (request == null)
      throw new IllegalArgumentException("Request body is required");
    if (request.maxWeight() <= 0)
      throw new IllegalArgumentException("maxWeight must be greater than 0");
    if (request.parts() == null || request.parts().isEmpty())
      throw new IllegalArgumentException("A tray must contain at least one part");

    double total = 0;
    int index = 0;
    for (PartRequest part : request.parts())
    {
      String where = "parts[" + index++ + "]";
      if (part == null)
        throw new IllegalArgumentException(where + " must not be null");
      if (part.animalEarmark() == null || part.animalEarmark().isBlank())
        throw new IllegalArgumentException(where + ".animalEarmark is required");
      if (part.partType() == null || part.partType().isBlank())
        throw new IllegalArgumentException(where + ".partType is required");
      if (part.weight() <= 0)
        throw new IllegalArgumentException(where + ".weight must be greater than 0");
      total += part.weight();
    }
    if (total > request.maxWeight() + WEIGHT_TOLERANCE)
      throw new IllegalArgumentException(
          "Parts weigh " + total + " kg, which exceeds the tray's maxWeight of " + request.maxWeight() + " kg");
    return total;
  }
}
