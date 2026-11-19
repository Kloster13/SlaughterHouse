package org.example.slaughterhousepackaging.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import partingmessaging.ProductType;

import java.util.List;
import java.util.UUID;

@Schema(description = "A product to register, with references to the trays its parts came from")
public record ProductRequest(
    @Schema(description = "Kind of product: PACKAGE (parts of the same kind) or HALF_ANIMAL (all expected parts)",
        example = "PACKAGE") ProductType productType,
    @Schema(description = "Ids of the trays the parts came from (at least one, no duplicates)",
        example = "[\"c0000000-0000-0000-0000-000000000001\"]") List<UUID> trayIds)
{
}
