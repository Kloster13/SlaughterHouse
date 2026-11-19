package registration.persistence;

public class NotFound extends RuntimeException
{
  public NotFound(String message)
  {
    super(message);
  }
}
