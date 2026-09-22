package server;

import com.google.type.DateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import server.domain.*;
import server.domain.Package;

import java.time.LocalDate;

public class MainTest
{
  public static void main(String[] args)
  {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("slaughterhousePU");
    EntityManager em = emf.createEntityManager();

    em.getTransaction().begin();
    Farm farm = new Farm("TestFarm");
    Animal animal = new Animal(farm, 200, AnimalType.COW);
    Tray tray = new Tray(200, 150);
    AnimalPart animalPart = new AnimalPart(animal, tray, 150, "leg");
    Package test = new Package("SOIJASDOIJASOIDJSAOIJD", LocalDate.now(), 220);
    test.getTrays().add(tray);
    em.persist(farm);
    System.out.println(0.2);
    em.persist(animal);
    System.out.println(0.22);
    em.persist(tray);
    System.out.println(0.25);
    em.persist(animalPart);
    System.out.println(0.5);
    em.persist(test);
    System.out.println(1);
    em.getTransaction().commit();
    System.out.println(2);
    em.close();
    emf.close();
  }
}
