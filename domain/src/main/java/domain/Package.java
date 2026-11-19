package domain;

import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Package extends Product
{
  public Package()
  {
  }

  public Package(UUID id, String serial, LocalDate packagedTime, double weight)
  {
    super(id, serial, packagedTime, weight);
  }

  public Package(String serial, LocalDate packagedTime, double weight)
  {
    super(serial, packagedTime, weight);
  }
}
