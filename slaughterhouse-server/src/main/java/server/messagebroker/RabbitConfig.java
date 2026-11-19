package server.messagebroker;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration public class RabbitConfig
{
  public static final String EXCHANGE_NAME = "house-exchange";
  public static final String PARTING_QUEUE_NAME ="parting-queue";
  public static final String PARTING_KEY_NAME = "parting-key";

  @Bean public MessageConverter messageConverter()
  {
    return new Jackson2JsonMessageConverter();
  }

  @Bean Exchange houseExchange()
  {
    return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
  }

  // Parting queue
  @Bean public Queue partingQueue()
  {
    return new Queue(PARTING_QUEUE_NAME, true);
  }
  @Bean Binding partingBinding()
  {
    return new Binding(PARTING_QUEUE_NAME, Binding.DestinationType.QUEUE, EXCHANGE_NAME, PARTING_KEY_NAME, null);
  }
}
