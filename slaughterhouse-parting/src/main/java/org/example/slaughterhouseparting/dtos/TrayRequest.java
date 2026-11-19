package org.example.slaughterhouseparting.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A tray to register, with the animal parts placed on it")
public record TrayRequest(
    @Schema(description = "Maximum weight the tray can hold in kg", example = "100") int maxWeight,
    @Schema(description = "Animal parts on the tray (at least one)") List<PartRequest> parts)
{
}
