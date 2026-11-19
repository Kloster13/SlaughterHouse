package org.example.slaughterhouseparting;

import org.example.slaughterhouseparting.persistence.PendingTray;
import org.example.slaughterhouseparting.persistence.PendingTrayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import partingmessaging.TrayRegistrationMessage;

import java.util.List;

@Component public class PendingTrayRetryJob
{
  private static final Logger log = LoggerFactory.getLogger(PendingTrayRetryJob.class);

  private final PendingTrayRepository pendingTrays;
  private final TrayMessageSender sender;

  public PendingTrayRetryJob(PendingTrayRepository pendingTrays, TrayMessageSender sender)
  {
    this.pendingTrays = pendingTrays;
    this.sender = sender;
  }

  @Scheduled(fixedDelayString = "${parting.retry-interval:30s}") public void resendPendingTrays()
  {
    List<PendingTray> pending = pendingTrays.findAllByOrderByCreatedAtAsc();
    if (pending.isEmpty())
      return;

    log.info("Resending {} pending tray(s)", pending.size());
    for (PendingTray tray : pending)
    {
      try
      {
        TrayRegistrationMessage message = sender.fromJson(tray.getPayload());
        sender.publish(message);
        pendingTrays.delete(tray);
        log.info("Resent pending tray {}", tray.getId());
      }
      catch (AmqpException e)
      {
        tray.recordFailedAttempt(e.getMessage());
        pendingTrays.save(tray);
        log.warn("RabbitMQ still unavailable, {} tray(s) remain pending", pending.size());
        return;
      }
    }
  }
}
