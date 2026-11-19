package registration;

import domain.Animal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import registration.dto.AnimalDto;
import registration.dto.RegisterRequest;
import registration.services.RegistrationService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Animals", description = "Registration and lookup of animals arriving at the slaughterhouse")
@RestController @RequestMapping("/animals") public class RegistrationController
{
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService)
  {
    this.registrationService = registrationService;
  }

  @Operation(summary = "Get an animal by earmark")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Animal found"),
      @ApiResponse(responseCode = "404", description = "No animal with the given earmark", content = @Content)})
  @GetMapping("/{earmark}") public ResponseEntity<Animal> getAnimal(
      @Parameter(description = "Earmark of the animal", example = "DK-12345-0001") @PathVariable String earmark)
  {
    return ResponseEntity.status(HttpStatus.OK).body(registrationService.getAnimal(earmark));
  }

  @Operation(summary = "List animals, optionally filtered by farm name and/or arrival date",
      description = "Both filters are optional. If both are given, only animals matching both are returned. "
          + "If none are given, all animals are returned.")
  @ApiResponse(responseCode = "200", description = "List of matching animals (may be empty)")
  @GetMapping public ResponseEntity<List<AnimalDto>> getAnimals(
      @Parameter(description = "Name of the farm the animal came from", example = "Nørgaard Gård")
      @RequestParam(required = false) String farmName,
      @Parameter(description = "Arrival date (ISO format yyyy-MM-dd)")
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
  {
    return ResponseEntity.ok(registrationService.getAnimalsByFarmNameOrDate(farmName,date));
  }

  @Operation(summary = "Register a new animal",
      description = "Registers an animal from the given farm. The arrival date is set to today by the server.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Animal registered"),
      @ApiResponse(responseCode = "404", description = "No farm with the given farmId", content = @Content)})
  @PostMapping public ResponseEntity<AnimalDto> registerAnimal(@RequestBody RegisterRequest request)
  {
    return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerAnimal(request));
  }
}
