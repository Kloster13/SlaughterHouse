package domain;

import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class HalfAnimal extends Product
{
  public HalfAnimal()
  {
  }

  public HalfAnimal(UUID id, String serial, LocalDate packagedDate, double weight)
  {
    super(id, serial, packagedDate, weight);
  }

}
