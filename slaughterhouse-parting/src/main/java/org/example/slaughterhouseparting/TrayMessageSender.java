package org.example.slaughterhouseparting;

import org.example.slaughterhouseparting.persistence.PendingTray;
import org.example.slaughterhouseparting.persistence.PendingTrayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import partingmessaging.TrayRegistrationMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import util.RabbitConstants;

@Component public class TrayMessageSender
{

  private static final Logger log = LoggerFactory.getLogger(TrayMessageSender.class);

  private final RabbitTemplate rabbitTemplate;
  private final PendingTrayRepository pendingTrays;
  private final JsonMapper jsonMapper;

  public TrayMessageSender(RabbitTemplate rabbitTemplate, PendingTrayRepository pendingTrays, JsonMapper jsonMapper)
  {
    this.rabbitTemplate = rabbitTemplate;
    this.pendingTrays = pendingTrays;
    this.jsonMapper = jsonMapper;
  }

  public void sendTray(TrayRegistrationMessage message)
  {
    try
    {
      publish(message);
      log.info("Sent tray {}", message.trayId());
    }
    catch (AmqpException e)
    {
      log.warn("RabbitMQ unavailable, storing tray {} for later: {}", message.trayId(), e.getMessage());
      pendingTrays.save(new PendingTray(message.trayId(), toJson(message), e.getMessage()));
    }
  }

  void publish(TrayRegistrationMessage message)
  {
    rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_NAME, RabbitConstants.PARTING_KEY_NAME, message);
  }

  String toJson(TrayRegistrationMessage message)
  {
    try
    {
      return jsonMapper.writeValueAsString(message);
    }
    catch (JacksonException e)
    {
      throw new IllegalStateException("Could not serialize tray " + message.trayId(), e);
    }
  }

  TrayRegistrationMessage fromJson(String json)
  {
    try
    {
      return jsonMapper.readValue(json, TrayRegistrationMessage.class);
    }
    catch (JacksonException e)
    {
      throw new IllegalStateException("Could not read stored tray", e);
    }
  }
}
