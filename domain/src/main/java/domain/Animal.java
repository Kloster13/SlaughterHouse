package domain;

import jakarta.persistence.*;

@Entity public class Animal
{
  @Id @GeneratedValue private Long id;

  @ManyToOne @JoinColumn(name = "farm_id") private Farm farm;
  @Enumerated(EnumType.STRING) private AnimalType type;
  private double weight;

  public Animal()
  {
  }

  public Animal(Farm farm, double weight, AnimalType type)
  {
    this.farm = farm;
    this.weight = weight;
    this.type = type;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Farm getFarm()
  {
    return farm;
  }

  public void setFarm(Farm farm)
  {
    this.farm = farm;
  }

  public AnimalType getType()
  {
    return type;
  }

  public void setType(AnimalType type)
  {
    this.type = type;
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
