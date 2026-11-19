package org.example.slaughterhousepackaging;

import org.example.slaughterhousepackaging.persistence.PendingProduct;
import org.example.slaughterhousepackaging.persistence.PendingProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import partingmessaging.ProductRegistrationMessage;

import java.util.List;

@Component public class PendingProductRetryJob
{
  private static final Logger log = LoggerFactory.getLogger(PendingProductRetryJob.class);

  private final PendingProductRepository pendingProducts;
  private final ProductMessageSender sender;

  public PendingProductRetryJob(PendingProductRepository pendingProducts, ProductMessageSender sender)
  {
    this.pendingProducts = pendingProducts;
    this.sender = sender;
  }

  @Scheduled(fixedDelayString = "${packaging.retry-interval:30s}") public void resendPendingProducts()
  {
    List<PendingProduct> pending = pendingProducts.findAllByOrderByCreatedAtAsc();
    if (pending.isEmpty())
      return;

    log.info("Resending {} pending product(s)", pending.size());
    for (PendingProduct product : pending)
    {
      try
      {
        ProductRegistrationMessage message = sender.fromJson(product.getPayload());
        sender.publish(message);
        pendingProducts.delete(product);
        log.info("Resent pending product {}", product.getId());
      }
      catch (AmqpException e)
      {
        product.recordFailedAttempt(e.getMessage());
        pendingProducts.save(product);
        log.warn("RabbitMQ still unavailable, {} product(s) remain pending", pending.size());
        return;
      }
    }
  }
}
