package server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

@Entity public class Tray
{
  @Id @GeneratedValue private Long id;
  @ManyToMany(mappedBy = "trays") private Set<Product> products;
  private int maxWeight;
  private double weight;

  public Tray()
  {
  }

  public Tray(int maxWeight, double weight)
  {
    this.products = new HashSet<>();
    this.maxWeight = maxWeight;
    this.weight = weight;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Set<Product> getProducts()
  {
    return products;
  }

  public void setProducts(Set<Product> products)
  {
    this.products = products;
  }

  public int getMaxWeight()
  {
    return maxWeight;
  }

  public void setMaxWeight(int maxWeight)
  {
    this.maxWeight = maxWeight;
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
