package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import domain.Product;
import server.persistence.NotFound;
import server.persistence.interfaces.ProductDao;

import java.util.List;
import java.util.UUID;

@Repository
public class ProductDaoDatabase implements ProductDao
{
  private final EntityManager em;

  public ProductDaoDatabase(EntityManager em)
  {
    this.em = em;
  }

  @Override public void createProduct(Product product)
  {
    em.persist(product);
  }

  @Override public void update(Product updatedProduct)
  {
    em.merge(updatedProduct);
  }

  @Override public Product getProductById(UUID id)
  {
    Product toFind = em.find(Product.class, id);
    if (toFind == null)
      throw new NotFound("Product not found");
    return toFind;
  }

  @Override public List<Product> getProductsByAnimalId(String earmark)
  {
    return em.createQuery("SELECT DISTINCT p FROM Product p " + "JOIN p.trays t "
            + "JOIN AnimalPart ap ON ap.tray = t " + "JOIN ap.animal a " + "WHERE a.earmark = :earmark",
        Product.class).setParameter("earmark", earmark).getResultList();
  }

  @Override public List<Product> getAllProducts()
  {
    return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
  }

  @Override public void deleteProduct(UUID id)
  {
    Product toRemove = getProductById(id);
    if (toRemove == null)
    {
      throw new NotFound("Product not found");
    }
    em.remove(toRemove);
  }
}
