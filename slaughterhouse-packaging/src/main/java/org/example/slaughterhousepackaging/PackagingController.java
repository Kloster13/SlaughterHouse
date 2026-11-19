package org.example.slaughterhousepackaging;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.slaughterhousepackaging.dtos.ProductRequest;
import org.example.slaughterhousepackaging.dtos.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Packaging", description = "Registration of packaged products")
@RestController @RequestMapping("/packaging") public class PackagingController
{
  private final PackagingService packagingService;

  public PackagingController(PackagingService packagingService)
  {
    this.packagingService = packagingService;
  }

  @Operation(summary = "Register a packaged product",
      description = "Validates the request, generates id, serial and packaging date, and publishes the product to "
          + "the packaging queue. The server registers it asynchronously and calculates the weight from the trays, "
          + "so an unknown tray id is not detected here.")
  @ApiResponses({@ApiResponse(responseCode = "202", description = "Product accepted and sent for registration"),
      @ApiResponse(responseCode = "400", description = "Invalid request (see message)", content = @Content)})
  @PostMapping("/products") public ResponseEntity<ProductResponse> registerProduct(
      @RequestBody ProductRequest request)
  {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(packagingService.registerProduct(request));
  }
}
