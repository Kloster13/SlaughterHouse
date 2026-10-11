package server;

import grpcslaughter.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import domain.Animal;
import domain.Product;
import server.dto.DTOFactory;
import server.sevices.TraceabilityService;

import java.util.List;

@Component
public class MainServerImpl extends InformationServiceGrpc.InformationServiceImplBase
{
  private final TraceabilityService traceService;

  public MainServerImpl(TraceabilityService traceService)
  {
    this.traceService = traceService;
  }

  @Override public void traceAnimalsFromProduct(TraceAnimalsFromProductRequest request,
      StreamObserver<TraceAnimalsFromProductResponse> responseObserver)
  {
    List<Animal> animals = traceService.traceAnimalsFromProduct(request.getProductId());
    responseObserver.onNext(DTOFactory.createAnimalTraceResponse(animals));
    responseObserver.onCompleted();
  }

  @Override public void traceProductsFromAnimal(TraceProductsFromAnimalRequest request,
      StreamObserver<TraceProductsFromAnimalResponse> responseObserver)
  {
    List<Product> products = traceService.traceProductFromAnimal(request.getAnimalId());
    responseObserver.onNext(DTOFactory.createProductTraceResponse(products));
    responseObserver.onCompleted();
  }
}
