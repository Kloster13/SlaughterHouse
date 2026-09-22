package server.dto;

import grpcslaughter.AnimalDTO;
import grpcslaughter.TraceAnimalsFromProductRequest;
import grpcslaughter.TraceAnimalsFromProductResponse;
import server.domain.Animal;

import java.util.ArrayList;
import java.util.List;

public class DTOFactory
{
  public static TraceAnimalsFromProductRequest createAnimalTraceRequest(long id)
  {
    return TraceAnimalsFromProductRequest.newBuilder().setProductId(id).build();
  }

  public static TraceAnimalsFromProductResponse createAnimalTraceResponse(List<Animal> animals)
  {
    ArrayList<AnimalDTO> listToReturn = new ArrayList<>();
    for (Animal a : animals)
    {
      listToReturn.add(
          AnimalDTO.newBuilder().setId(a.getId()).setType(a.getType().toString()).setWeight(a.getWeight())
              .setFarmName(a.getFarm().getName()).build());
    }
    return TraceAnimalsFromProductResponse.newBuilder().addAllAnimals(listToReturn).build();
  }
}
