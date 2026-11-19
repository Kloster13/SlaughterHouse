package org.example.slaughterhouseparting.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "One animal part placed on a tray")
public record PartRequest(
    @Schema(description = "Earmark of the animal the part comes from (must be registered)", example = "DK-1234-0001")
    String animalEarmark,
    @Schema(description = "Weight of the part in kg", example = "20.5") double weight,
    @Schema(description = "Type of part", example = "Leg") String partType)
{
}
