package org.example.slaughterhouseparting;

import org.example.slaughterhouseparting.persistence.PendingTray;
import org.example.slaughterhouseparting.persistence.PendingTrayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import partingmessaging.AnimalPartRegistration;
import partingmessaging.TrayRegistrationMessage;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TrayFallbackTest
{
  private RabbitTemplate rabbitTemplate;
  private PendingTrayRepository repository;
  private TrayMessageSender sender;
  private TrayRegistrationMessage message;

  @BeforeEach void setUp()
  {
    rabbitTemplate = mock(RabbitTemplate.class);
    repository = mock(PendingTrayRepository.class);
    sender = new TrayMessageSender(rabbitTemplate, repository, JsonMapper.builder().build());
    message = new TrayRegistrationMessage(UUID.randomUUID(), 100, 20.5,
        List.of(new AnimalPartRegistration(UUID.randomUUID(), "DK-1234-0001", 20.5, "Leg")));
  }

  @Test void sendTray_doesNotStoreWhenRabbitIsUp()
  {
    sender.sendTray(message);

    verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(message));
    verifyNoInteractions(repository);
  }

  @Test void sendTray_storesTrayWhenRabbitIsDown()
  {
    doThrow(new AmqpConnectException(new RuntimeException("refused"))).when(rabbitTemplate)
        .convertAndSend(anyString(), anyString(), any(Object.class));

    assertDoesNotThrow(() -> sender.sendTray(message));

    var captor = org.mockito.ArgumentCaptor.forClass(PendingTray.class);
    verify(repository).save(captor.capture());
    assertEquals(message.trayId(), captor.getValue().getId());
    assertEquals(message, sender.fromJson(captor.getValue().getPayload()));
  }

  @Test void retryJob_resendsAndDeletesPendingTray()
  {
    PendingTray pending = new PendingTray(message.trayId(), sender.toJson(message), "down");
    when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(pending));

    new PendingTrayRetryJob(repository, sender).resendPendingTrays();

    verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(message));
    verify(repository).delete(pending);
  }

  @Test void retryJob_keepsTrayAndStopsWhenRabbitStillDown()
  {
    PendingTray pending = new PendingTray(message.trayId(), sender.toJson(message), "down");
    when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(pending));
    doThrow(new AmqpConnectException(new RuntimeException("refused"))).when(rabbitTemplate)
        .convertAndSend(anyString(), anyString(), any(Object.class));

    new PendingTrayRetryJob(repository, sender).resendPendingTrays();

    verify(repository, never()).delete(any(PendingTray.class));
    verify(repository).save(pending);
    assertEquals(2, pending.getAttempts());
  }
}
