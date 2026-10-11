package registration.dto;

import domain.AnimalType;

import java.time.LocalDate;

public record AnimalDto(long id, String farmName, LocalDate arrivalDate, AnimalType animalType, double weight)
{
}
