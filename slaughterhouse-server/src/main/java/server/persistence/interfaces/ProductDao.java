package server.persistence.interfaces;

import domain.Product;

import java.util.List;
import java.util.UUID;

public interface ProductDao
{
  void createProduct(Product product);
  void update(Product updatedProduct);
  boolean existsById(UUID id);
  Product getProductById(UUID id);
  List<Product> getProductsByAnimalId(String earmark);
  List<Product> getAllProducts();
  void deleteProduct(UUID id);
}
