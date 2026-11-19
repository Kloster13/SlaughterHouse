package org.example.slaughterhousepackaging.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

/** A product message that could not be sent to RabbitMQ and is waiting to be resent. */
@Entity public class PendingProduct
{
  @Id private UUID id;
  @Column(nullable = false, length = 100_000) private String payload;
  @Column(nullable = false) private Instant createdAt;
  private int attempts;
  @Column(length = 1000) private String lastError;

  protected PendingProduct()
  {
  }

  public PendingProduct(UUID id, String payload, String lastError)
  {
    this.id = id;
    this.payload = payload;
    this.createdAt = Instant.now();
    this.attempts = 1;
    this.lastError = lastError;
  }

  public UUID getId()
  {
    return id;
  }

  public String getPayload()
  {
    return payload;
  }

  public Instant getCreatedAt()
  {
    return createdAt;
  }

  public int getAttempts()
  {
    return attempts;
  }

  public String getLastError()
  {
    return lastError;
  }

  public void recordFailedAttempt(String error)
  {
    this.attempts++;
    this.lastError = error;
  }
}
