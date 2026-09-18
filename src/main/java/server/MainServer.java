package server;

import com.sun.tools.javac.Main;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class MainServer
{
  public static void main(String[] args) throws Exception
  {
    Server server = ServerBuilder.forPort(9090).addService(new MainServerImpl()).build();

    server.start();
    server.awaitTermination();
  }
}
