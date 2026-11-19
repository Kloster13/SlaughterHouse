package server.messagebroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import partingmessaging.ProductRegistrationMessage;
import server.persistence.NotFound;
import server.sevices.PackagingService;
import util.RabbitConstants;

@Component @RabbitListener(queues = RabbitConstants.PACKAGING_QUEUE_NAME) public class PackagingQueueListener
{
  private static final Logger log = LoggerFactory.getLogger(PackagingQueueListener.class);
  private final PackagingService packagingService;

  public PackagingQueueListener(PackagingService packagingService)
  {
    this.packagingService = packagingService;
  }

  @RabbitHandler public void handleProductRegistration(ProductRegistrationMessage message)
  {
    log.info("Product registration received with id {}", message.productId());
    try
    {
      packagingService.registerProduct(message);
    }
    catch (NotFound | IllegalArgumentException | IllegalStateException e)
    {
      // Permanent errors: retrying the same message can never succeed, so drop it instead of requeueing forever
      log.error("Rejecting product registration {}: {}", message.productId(), e.getMessage());
      throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
    log.info("Product registration with id {} completed", message.productId());
  }
}
