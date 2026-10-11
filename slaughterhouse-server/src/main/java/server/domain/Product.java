package server.domain;

import com.google.type.DateTime;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) public abstract class Product
{
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) private long id;
  @ManyToMany @JoinTable(name = "product_tray", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "tray_id")) private Set<Tray> trays;
  private String serial;
  private LocalDate packagedDate;
  private double weight;

  public Product()
  {
  }

  public Product(String serial, LocalDate packagedTime, double weight)
  {
    this.trays = new HashSet<>();
    this.serial = serial;
    this.packagedDate = packagedTime;
    this.weight = weight;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public Set<Tray> getTrays()
  {
    return trays;
  }

  public void setTrays(Set<Tray> trays)
  {
    this.trays = trays;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }

  public String getSerial()
  {
    return serial;
  }

  public void setSerial(String serial)
  {
    this.serial = serial;
  }

  public LocalDate getPackagedDate()
  {
    return packagedDate;
  }

  public void setPackagedDate(LocalDate packagedDate)
  {
    this.packagedDate = packagedDate;
  }
}
