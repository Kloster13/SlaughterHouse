package registration.dto;

import domain.AnimalType;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data needed to register a new animal")
public record RegisterRequest(
    @Schema(description = "Earmark of the animal (its unique id)", example = "DK-12345-0001") String earmark,
    @Schema(description = "Id of the farm the animal comes from") UUID farmId,
    @Schema(description = "Type of animal", example = "PIG") AnimalType animalType,
    @Schema(description = "Weight in kg", example = "120.5") double weight)
{
}
