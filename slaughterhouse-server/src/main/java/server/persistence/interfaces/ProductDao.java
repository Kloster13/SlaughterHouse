package server.persistence.interfaces;

import server.domain.Product;

import java.util.List;

public interface ProductDao
{
  void createProduct(Product product);
  void update(Product updatedProduct);
  Product getProductById(long id);
  List<Product> getProductsByAnimalId(long animalId);
  List<Product> getAllProducts();
  void deleteProduct(long id);
}
