package registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import registration.persistence.NotFound;

import java.time.Instant;

@RestControllerAdvice public class GlobalExceptionHandler
{
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  public record ErrorResponse(int status, String error, String message, Instant timestamp)
  {
  }

  @ExceptionHandler(NotFound.class) public ResponseEntity<ErrorResponse> handleNotFound(NotFound e)
  {
    return build(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<ErrorResponse> handleBadRequest(
      IllegalArgumentException e)
  {
    return build(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class) public ResponseEntity<ErrorResponse> handleUnexpected(RuntimeException e)
  {
    log.error("Unexpected error", e);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }

  private static ResponseEntity<ErrorResponse> build(HttpStatus status, String message)
  {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now()));
  }
}
