package server.persistence.databaseimpl;

import jakarta.persistence.EntityManager;
import server.domain.Product;
import server.persistence.NotFound;
import server.persistence.interfaces.ProductDao;

import java.util.List;

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

  @Override public Product getProductById(long id)
  {
    Product toFind = em.find(Product.class, id);
    if (toFind == null)
      throw new NotFound("Product not found");
    return toFind;
  }

  @Override public List<Product> getAllProducts()
  {
    return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
  }

  @Override public void deleteProduct(long id)
  {
    Product toRemove = getProductById(id);
    if (toRemove == null)
    {
      throw new NotFound("Product not found");
    }
    em.remove(toRemove);
  }
}
