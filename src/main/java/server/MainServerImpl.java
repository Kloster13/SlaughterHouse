package server;

import grpcslaughter.InformationServiceGrpc;
import grpcslaughter.GetAnimalRequest;
import grpcslaughter.GetAnimalResponse;
import io.grpc.stub.StreamObserver;

public class MainServerImpl extends InformationServiceGrpc.InformationServiceImplBase
{
  public MainServerImpl()
  {
  }

  @Override public void getAnimal(GetAnimalRequest request, StreamObserver<GetAnimalResponse> responseObserver)
  {
    GetAnimalResponse dinMor = GetAnimalResponse.newBuilder().setName("Hej").build();
    responseObserver.onNext(dinMor);
    responseObserver.onCompleted();
  }
}
