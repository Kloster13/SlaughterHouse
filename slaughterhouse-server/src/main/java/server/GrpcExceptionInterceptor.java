package server;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.persistence.NotFound;

/**
 * gRPC counterpart to a REST @ControllerAdvice: translates exceptions thrown by the service
 * implementation into gRPC statuses so the client always gets a proper error response.
 */
public class GrpcExceptionInterceptor implements ServerInterceptor
{
  private static final Logger log = LoggerFactory.getLogger(GrpcExceptionInterceptor.class);

  @Override public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
      Metadata headers, ServerCallHandler<ReqT, RespT> next)
  {
    return new SimpleForwardingServerCallListener<>(next.startCall(call, headers))
    {
      @Override public void onHalfClose()
      {
        try
        {
          super.onHalfClose();
        }
        catch (RuntimeException e)
        {
          call.close(toStatus(e), new Metadata());
        }
      }

      @Override public void onMessage(ReqT message)
      {
        try
        {
          super.onMessage(message);
        }
        catch (RuntimeException e)
        {
          call.close(toStatus(e), new Metadata());
        }
      }
    };
  }

  private static Status toStatus(RuntimeException e)
  {
    if (e instanceof NotFound)
      return Status.NOT_FOUND.withDescription(e.getMessage());
    if (e instanceof IllegalArgumentException)
      return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    log.error("Unexpected error in gRPC call", e);
    return Status.INTERNAL.withDescription("Internal server error");
  }
}
