package org.example.slaughterhouseparting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.slaughterhouseparting.dtos.TrayRequest;
import org.example.slaughterhouseparting.dtos.TrayResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Parting", description = "Registration of trays with animal parts")
@RestController @RequestMapping("/parting") public class PartingController
{
  private final PartingService partingService;

  public PartingController(PartingService partingService)
  {
    this.partingService = partingService;
  }

  @Operation(summary = "Register a tray with animal parts",
      description = "Validates the tray, generates ids and publishes it to the parting queue. "
          + "The server registers it asynchronously, so an unknown earmark is not detected here.")
  @ApiResponses({@ApiResponse(responseCode = "202", description = "Tray accepted and sent for registration"),
      @ApiResponse(responseCode = "400", description = "Invalid tray (see message)", content = @Content)})
  @PostMapping("/trays") public ResponseEntity<TrayResponse> registerTray(@RequestBody TrayRequest request)
  {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(partingService.registerTrayWithParts(request));
  }
}
