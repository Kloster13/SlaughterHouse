package partingmessaging;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProductRegistrationMessage(UUID productId, ProductType productType, String serial,
                                         LocalDate packagedDate, List<UUID> trayIds)
{
}
