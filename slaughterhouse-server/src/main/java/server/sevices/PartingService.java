package server.sevices;

import domain.Animal;
import domain.AnimalPart;
import domain.Tray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partingmessaging.AnimalPartRegistration;
import partingmessaging.TrayRegistrationMessage;
import server.persistence.NotFound;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.TrayDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service public class PartingService
{
  private static final Logger log = LoggerFactory.getLogger(PartingService.class);
  private final TrayDao trayDao;
  private final AnimalPartDao animalPartDao;
  private final AnimalDao animalDao;

  public PartingService(TrayDao trayDao, AnimalPartDao animalPartDao, AnimalDao animalDao)
  {

    this.trayDao = trayDao;
    this.animalPartDao = animalPartDao;
    this.animalDao = animalDao;
  }

  private static final double WEIGHT_TOLERANCE = 0.001;

  private static void validate(TrayRegistrationMessage message)
  {
    if (message == null || message.trayId() == null)
      throw new IllegalArgumentException("Tray registration must have a trayId");
    if (message.animalParts() == null || message.animalParts().isEmpty())
      throw new IllegalArgumentException("Tray " + message.trayId() + " has no animal parts");
    if (message.maxWeight() <= 0 || message.weight() <= 0)
      throw new IllegalArgumentException("Tray " + message.trayId() + " must have positive weight and maxWeight");
    if (message.weight() > message.maxWeight())
      throw new IllegalArgumentException(
          "Tray " + message.trayId() + " weight " + message.weight() + " exceeds maxWeight " + message.maxWeight());

    Set<java.util.UUID> partIds = new HashSet<>();
    double sum = 0;
    for (AnimalPartRegistration part : message.animalParts())
    {
      if (part == null || part.partId() == null || part.animalEarmark() == null || part.animalEarmark().isBlank())
        throw new IllegalArgumentException("Animal part must have partId and animalEarmark");
      if (part.weight() <= 0)
        throw new IllegalArgumentException("Animal part " + part.partId() + " must have positive weight");
      if (!partIds.add(part.partId()))
        throw new IllegalArgumentException("Duplicate animal part id in message: " + part.partId());
      sum += part.weight();
    }
    if (sum > message.weight() + WEIGHT_TOLERANCE)
      throw new IllegalArgumentException(
          "Animal parts weigh " + sum + " but tray " + message.trayId() + " only weighs " + message.weight());
  }

  @Transactional public void registerTrayWithAnimalParts(TrayRegistrationMessage message)
  {
    validate(message);
    if (trayDao.existsById(message.trayId()))
    {
      log.warn("Tray {} already registered, ignoring message", message.trayId());
      return;
    }

    Tray tray = new Tray(message.trayId(), message.maxWeight(), message.weight());
    trayDao.createTray(tray);

    Map<String, Animal> animals = new HashMap<>();

    for (AnimalPartRegistration part : message.animalParts())
    {
      if (animalPartDao.existsById(part.partId()))
      {
        throw new IllegalStateException("Animal part already exists: " + part.partId());
      }

      Animal animal = animals.computeIfAbsent(part.animalEarmark(),
          earmark -> animalDao.getAnimalByEarMark(earmark)
              .orElseThrow(() -> new NotFound("Animal not found: " + earmark)));

      animalPartDao.createAnimalPart(
          new AnimalPart(part.partId(), animal, tray, part.weight(), part.partType()));
    }
  }
}