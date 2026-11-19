package server.messagebroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import partingmessaging.TrayRegistrationMessage;
import server.persistence.NotFound;
import server.sevices.PartingService;

@Component @RabbitListener(queues = RabbitConfig.PARTING_QUEUE_NAME) public class PartingQueueListener
{
  private static final Logger log = LoggerFactory.getLogger(PartingQueueListener.class);
  private final PartingService partingService;

  public PartingQueueListener(PartingService partingService)
  {
    this.partingService = partingService;
  }

  @RabbitHandler public void handlePartTrayRegistration(TrayRegistrationMessage message)
  {
    log.info("Tray registration received with id {}",message.trayId());
    try
    {
      partingService.registerTrayWithAnimalParts(message);
    }
    catch (NotFound | IllegalArgumentException | IllegalStateException e)
    {
      // Permanent errors: retrying the same message can never succeed, so drop it instead of requeueing forever
      log.error("Rejecting tray registration {}: {}", message.trayId(), e.getMessage());
      throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
    log.info("Tray registration with id {} completed", message.trayId());
  }
}
