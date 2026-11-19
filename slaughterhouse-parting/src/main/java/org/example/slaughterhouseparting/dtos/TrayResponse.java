package org.example.slaughterhouseparting.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Confirmation that a tray was sent for registration")
public record TrayResponse(
    @Schema(description = "Generated id of the tray") UUID trayId,
    @Schema(description = "Total weight of the parts on the tray in kg", example = "80.5") double weight,
    @Schema(description = "Number of animal parts on the tray", example = "4") int partCount)
{
}
