package registration.dto;

import domain.AnimalType;

public record RegisterRequest(long farmId, AnimalType animalType, double weight)
{
}
