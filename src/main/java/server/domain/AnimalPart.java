package server.domain;

import jakarta.persistence.*;

@Entity
public class AnimalPart
{
  @Id @GeneratedValue private Long id;
  @ManyToOne @JoinColumn(name = "animal_id") private Animal animal;
  @ManyToOne @JoinColumn(name = "tray_id") private Tray tray;
  private double weight;
  private String partType;

  public AnimalPart()
  {
  }

  public AnimalPart(Animal animal, Tray tray, double weight, String partType)
  {
    this.animal = animal;
    this.tray = tray;
    this.weight = weight;
    this.partType = partType;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Animal getAnimal()
  {
    return animal;
  }

  public void setAnimal(Animal animal)
  {
    this.animal = animal;
  }

  public Tray getTray()
  {
    return tray;
  }

  public void setTray(Tray tray)
  {
    this.tray = tray;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }

  public String getPartType()
  {
    return partType;
  }

  public void setPartType(String partType)
  {
    this.partType = partType;
  }
}
