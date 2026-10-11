package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServerRunner implements SmartLifecycle
{
  private final MainServerImpl service;
  private final int port;
  private Server server;

  public GrpcServerRunner(MainServerImpl service, @Value("${grpc.port}") int port)
  {
    this.service = service;
    this.port = port;
  }

  @Override public void start()
  {
    try
    {
      server = ServerBuilder.forPort(port).addService(service).build().start();
      Thread awaitThread = new Thread(() -> {
        try
        {
          server.awaitTermination();
        }
        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
        }
      }, "grpc-await");
      awaitThread.setDaemon(false);
      awaitThread.start();
    }
    catch (IOException e)
    {
      throw new IllegalStateException("Could not start gRPC server on port " + port, e);
    }
  }

  @Override public void stop()
  {
    if (server != null)
      server.shutdown();
  }

  @Override public boolean isRunning()
  {
    return server != null && !server.isShutdown();
  }
}
