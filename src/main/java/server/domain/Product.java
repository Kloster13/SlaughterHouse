package server.domain;

import jakarta.persistence.*;

import java.util.Set;

@Entity @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Product
{
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) private long id;
  @ManyToMany
  @JoinTable(
      name = "product_tray",
      joinColumns = @JoinColumn(name="product_id"),
      inverseJoinColumns = @JoinColumn(name="tray_id")
  )
  private Set<Tray> trays;
  private double weight;

  public Product()
  {
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
}
