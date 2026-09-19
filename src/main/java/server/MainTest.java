package server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import server.domain.*;

public class MainTest
{
  public static void main(String[] args)
  {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("slaughterhousePU");
    EntityManager em = emf.createEntityManager();

    em.getTransaction().begin();
    Farm farm = new Farm("TestFarm");
    Animal animal = new Animal(farm,200, AnimalType.COW);
    Tray tray = new Tray(200,150);
    AnimalPart animalPart = new AnimalPart(animal,tray,150,"leg");

    em.persist(farm);
    em.persist(animal);
    em.persist(tray);
    em.persist(animalPart);

    em.getTransaction().commit();

    em.close();
    emf.close();
  }
}
