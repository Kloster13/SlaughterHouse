package server.domain;

import com.google.type.DateTime;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Package extends Product
{
  public Package()
  {
  }

  public Package(String serial, LocalDate packagedTime, double weight)
  {
    super(serial, packagedTime, weight);
  }
}
