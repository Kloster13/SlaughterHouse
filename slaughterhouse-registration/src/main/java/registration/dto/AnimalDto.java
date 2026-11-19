package registration.dto;

import domain.AnimalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "A registered animal")
public record AnimalDto(
    @Schema(description = "Earmark of the animal", example = "DK-12345-0001") String earmark,
    @Schema(description = "Name of the farm the animal came from", example = "Sunny Farm") String farmName,
    @Schema(description = "Date the animal arrived at the slaughterhouse", example = "2026-10-14") LocalDate arrivalDate,
    @Schema(description = "Type of animal", example = "PIG") AnimalType animalType,
    @Schema(description = "Weight in kg", example = "120.5") double weight)
{
}
