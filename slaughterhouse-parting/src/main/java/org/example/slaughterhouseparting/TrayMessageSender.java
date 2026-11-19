package org.example.slaughterhouseparting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import partingmessaging.TrayRegistrationMessage;
import util.RabbitConstants;

@Component public class TrayMessageSender
{

  private static final Logger log = LoggerFactory.getLogger(TrayMessageSender.class);

  private final RabbitTemplate rabbitTemplate;

  public TrayMessageSender(RabbitTemplate rabbitTemplate)
  {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendTray(TrayRegistrationMessage message)
  {
    rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_NAME, RabbitConstants.PARTING_KEY_NAME, message);
    log.info("Sent tray {}", message.trayId());
  }
}
