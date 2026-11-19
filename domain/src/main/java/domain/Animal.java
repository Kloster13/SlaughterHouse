package domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity public class Animal
{
  @Id private String earmark;
  @ManyToOne @JoinColumn(name = "farm_id") private Farm farm;
  private LocalDate arrivalDate;
  @Enumerated(EnumType.STRING) private AnimalType type;
  private double weight;
  public Animal()
  {
  }

  public Animal(String earmark, Farm farm,LocalDate arrivalDate, double weight, AnimalType type)
  {
    this.earmark = earmark;
    this.farm = farm;
    this.arrivalDate=arrivalDate;
    this.weight = weight;
    this.type = type;
  }

  public LocalDate getArrivalDate()
  {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate)
  {
    this.arrivalDate = arrivalDate;
  }

  public String getEarmark()
  {
    return earmark;
  }

  public void setEarmark(String earmark)
  {
    this.earmark = earmark;
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
