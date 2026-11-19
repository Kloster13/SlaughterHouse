package server.dto;

import grpcslaughter.*;
import domain.Animal;
import domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DTOFactory
{
  public static TraceAnimalsFromProductRequest createAnimalTraceRequest(UUID id)
  {
    return TraceAnimalsFromProductRequest.newBuilder().setProductId(id.toString()).build();
  }

  public static TraceAnimalsFromProductResponse createAnimalTraceResponse(List<Animal> animals)
  {
    ArrayList<AnimalDTO> listToReturn = new ArrayList<>();
    for (Animal a : animals)
    {
      listToReturn.add(AnimalDTO.newBuilder().setEarmark(a.getEarmark()).setType(a.getType().toString())
          .setArrivalDate(a.getArrivalDate().toString()).setWeight(a.getWeight())
          .setFarmName(a.getFarm().getName()).build());
    }
    return TraceAnimalsFromProductResponse.newBuilder().addAllAnimals(listToReturn).build();
  }

  public static TraceProductsFromAnimalRequest createProductTraceRequest(String earmark)
  {
    return TraceProductsFromAnimalRequest.newBuilder().setAnimalId(earmark).build();
  }

  public static TraceProductsFromAnimalResponse createProductTraceResponse(List<Product> products)
  {
    ArrayList<ProductDto> productList = new ArrayList<>();
    for (Product p : products)
    {
      productList.add(
          ProductDto.newBuilder().setSerial(p.getSerial()).setPackagedDate(p.getPackagedDate().toString())
              .setWeight(p.getWeight()).build());
    }
    return TraceProductsFromAnimalResponse.newBuilder().addAllProducts(productList).build();
  }
}
