package server;

import grpcslaughter.InformationServiceGrpc;
import grpcslaughter.TraceAnimalsFromProductRequest;
import grpcslaughter.TraceAnimalsFromProductResponse;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import server.domain.Animal;
import server.dto.DTOFactory;
import server.persistence.databaseimpl.AnimalDaoDatabase;
import server.persistence.databaseimpl.AnimalPartDaoDatabase;
import server.persistence.databaseimpl.ProductDaoDatabase;
import server.persistence.databaseimpl.TrayDaoDatabase;
import server.persistence.interfaces.AnimalDao;
import server.persistence.interfaces.AnimalPartDao;
import server.persistence.interfaces.ProductDao;
import server.persistence.interfaces.TrayDao;
import server.sevices.TraceabilityService;

import java.util.List;

public class MainServerImpl extends InformationServiceGrpc.InformationServiceImplBase
{
  private final TraceabilityService traceService;

  public MainServerImpl()
  {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("slaughterhousePU");
    EntityManager em = emf.createEntityManager();
    AnimalDao animalDao  = new AnimalDaoDatabase(em);
    AnimalPartDao animalPartDao = new AnimalPartDaoDatabase(em);
    TrayDao trayDao = new TrayDaoDatabase(em);
    ProductDao productDao = new ProductDaoDatabase(em);
    traceService = new TraceabilityService(animalDao,animalPartDao,productDao,trayDao,em);
  }

  @Override public void traceAnimalsFromProduct(TraceAnimalsFromProductRequest request,
      StreamObserver<TraceAnimalsFromProductResponse> responseObserver)
  {
    List<Animal> animals = traceService.traceAnimalsFromProduct(request.getProductId());
    responseObserver.onNext(DTOFactory.createAnimalTraceResponse(animals));
    responseObserver.onCompleted();
  }
}
