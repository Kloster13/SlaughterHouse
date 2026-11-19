package org.example.slaughterhousepackaging.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import partingmessaging.ProductType;

import java.util.UUID;

@Schema(description = "Confirmation that a product was sent for registration")
public record ProductResponse(
    @Schema(description = "Generated id of the product") UUID productId,
    @Schema(description = "Generated serial number", example = "PKG-3F9A1C2B") String serial,
    @Schema(description = "Kind of product", example = "PACKAGE") ProductType productType,
    @Schema(description = "Number of trays the product references", example = "2") int trayCount)
{
}
