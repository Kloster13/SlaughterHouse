package org.example.slaughterhouseparting;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.RabbitConstants;

@Configuration public class RabbitConfig
{
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }
  @Bean Exchange houseExchange()
  {
    return ExchangeBuilder.topicExchange(RabbitConstants.EXCHANGE_NAME).durable(true).build();
  }

  // Parting queue
  @Bean public Queue partingQueue()
  {
    return new Queue(RabbitConstants.PARTING_QUEUE_NAME, true);
  }
  @Bean Binding partingBinding()
  {
    return new Binding(RabbitConstants.PARTING_QUEUE_NAME, Binding.DestinationType.QUEUE, RabbitConstants.EXCHANGE_NAME, RabbitConstants.PARTING_KEY_NAME, null);
  }
}
