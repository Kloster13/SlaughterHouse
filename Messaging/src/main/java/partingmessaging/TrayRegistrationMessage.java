package partingmessaging;

import java.util.List;
import java.util.UUID;

public record TrayRegistrationMessage(UUID trayId, int maxWeight, double weight,
                                            List<AnimalPartRegistration> animalParts)
{
}
