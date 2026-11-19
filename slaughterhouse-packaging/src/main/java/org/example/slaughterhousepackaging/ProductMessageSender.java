package org.example.slaughterhousepackaging;

import org.example.slaughterhousepackaging.persistence.PendingProduct;
import org.example.slaughterhousepackaging.persistence.PendingProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import partingmessaging.ProductRegistrationMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import util.RabbitConstants;

@Component public class ProductMessageSender
{
  private static final Logger log = LoggerFactory.getLogger(ProductMessageSender.class);

  private final RabbitTemplate rabbitTemplate;
  private final PendingProductRepository pendingProducts;
  private final JsonMapper jsonMapper;

  public ProductMessageSender(RabbitTemplate rabbitTemplate, PendingProductRepository pendingProducts,
      JsonMapper jsonMapper)
  {
    this.rabbitTemplate = rabbitTemplate;
    this.pendingProducts = pendingProducts;
    this.jsonMapper = jsonMapper;
  }

  public void sendProduct(ProductRegistrationMessage message)
  {
    try
    {
      publish(message);
      log.info("Sent product {}", message.productId());
    }
    catch (AmqpException e)
    {
      log.warn("RabbitMQ unavailable, storing product {} for later: {}", message.productId(), e.getMessage());
      pendingProducts.save(new PendingProduct(message.productId(), toJson(message), e.getMessage()));
    }
  }

  void publish(ProductRegistrationMessage message)
  {
    rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_NAME, RabbitConstants.PACKAGING_KEY_NAME, message);
  }

  String toJson(ProductRegistrationMessage message)
  {
    try
    {
      return jsonMapper.writeValueAsString(message);
    }
    catch (JacksonException e)
    {
      throw new IllegalStateException("Could not serialize product " + message.productId(), e);
    }
  }

  ProductRegistrationMessage fromJson(String json)
  {
    try
    {
      return jsonMapper.readValue(json, ProductRegistrationMessage.class);
    }
    catch (JacksonException e)
    {
      throw new IllegalStateException("Could not read stored product", e);
    }
  }
}
