package org.example.slaughterhousepackaging;

import org.example.slaughterhousepackaging.dtos.ProductRequest;
import org.example.slaughterhousepackaging.dtos.ProductResponse;
import org.example.slaughterhousepackaging.persistence.PendingProduct;
import org.example.slaughterhousepackaging.persistence.PendingProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import partingmessaging.ProductRegistrationMessage;
import partingmessaging.ProductType;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PackagingTest
{
  private RabbitTemplate rabbitTemplate;
  private PendingProductRepository repository;
  private ProductMessageSender sender;
  private PackagingService service;
  private final UUID tray1 = UUID.randomUUID();
  private final UUID tray2 = UUID.randomUUID();

  @BeforeEach void setUp()
  {
    rabbitTemplate = mock(RabbitTemplate.class);
    repository = mock(PendingProductRepository.class);
    sender = new ProductMessageSender(rabbitTemplate, repository, JsonMapper.builder().build());
    service = new PackagingService(sender);
  }

  private void rabbitIsDown()
  {
    doThrow(new AmqpConnectException(new RuntimeException("refused"))).when(rabbitTemplate)
        .convertAndSend(anyString(), anyString(), any(Object.class));
  }

  // ---------- service ----------

  @Test void registerProduct_sendsMessageAndReturnsGeneratedValues()
  {
    ProductResponse response = service.registerProduct(new ProductRequest(ProductType.PACKAGE, List.of(tray1, tray2)));

    ArgumentCaptor<ProductRegistrationMessage> captor = ArgumentCaptor.forClass(ProductRegistrationMessage.class);
    verify(rabbitTemplate).convertAndSend(anyString(), eq("packaging-key"), captor.capture());
    ProductRegistrationMessage sent = captor.getValue();
    assertEquals(response.productId(), sent.productId());
    assertEquals(ProductType.PACKAGE, sent.productType());
    assertTrue(sent.serial().startsWith("PKG-"));
    assertEquals(response.serial(), sent.serial());
    assertEquals(LocalDate.now(), sent.packagedDate());
    assertEquals(List.of(tray1, tray2), sent.trayIds());
    assertEquals(2, response.trayCount());
  }

  @Test void registerProduct_halfAnimalGetsHalfPrefix()
  {
    ProductResponse response = service.registerProduct(new ProductRequest(ProductType.HALF_ANIMAL, List.of(tray1)));

    assertTrue(response.serial().startsWith("HALF-"));
  }

  @Test void registerProduct_rejectsInvalidRequests()
  {
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(null));
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(new ProductRequest(null, List.of(tray1))));
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(new ProductRequest(ProductType.PACKAGE, null)));
    assertThrows(IllegalArgumentException.class, () -> service.registerProduct(new ProductRequest(ProductType.PACKAGE, List.of())));
    assertThrows(IllegalArgumentException.class,
        () -> service.registerProduct(new ProductRequest(ProductType.PACKAGE, Arrays.asList(tray1, null))));
    assertThrows(IllegalArgumentException.class,
        () -> service.registerProduct(new ProductRequest(ProductType.PACKAGE, List.of(tray1, tray1))));
    verifyNoInteractions(rabbitTemplate, repository);
  }

  // ---------- sender fallback ----------

  @Test void sendProduct_storesProductWhenRabbitIsDown()
  {
    rabbitIsDown();

    assertDoesNotThrow(() -> service.registerProduct(new ProductRequest(ProductType.PACKAGE, List.of(tray1))));

    verify(repository).save(any(PendingProduct.class));
  }

  @Test void sendProduct_doesNotStoreWhenRabbitIsUp()
  {
    service.registerProduct(new ProductRequest(ProductType.PACKAGE, List.of(tray1)));

    verifyNoInteractions(repository);
  }

  // ---------- JSON ----------

  @Test void json_roundTripKeepsMessageAndWritesDateAsIsoString()
  {
    ProductRegistrationMessage message = new ProductRegistrationMessage(UUID.randomUUID(), ProductType.HALF_ANIMAL,
        "HALF-1", LocalDate.of(2026, 11, 19), List.of(tray1));

    String json = sender.toJson(message);

    assertTrue(json.contains("\"2026-11-19\""), json);
    assertEquals(message, sender.fromJson(json));
  }

  // ---------- retry job ----------

  @Test void retryJob_resendsAndDeletesPendingProduct()
  {
    ProductRegistrationMessage message = new ProductRegistrationMessage(UUID.randomUUID(), ProductType.PACKAGE,
        "PKG-1", LocalDate.now(), List.of(tray1));
    PendingProduct pending = new PendingProduct(message.productId(), sender.toJson(message), "down");
    when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(pending));

    new PendingProductRetryJob(repository, sender).resendPendingProducts();

    verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(message));
    verify(repository).delete(pending);
  }

  @Test void retryJob_keepsProductAndStopsWhenRabbitStillDown()
  {
    ProductRegistrationMessage message = new ProductRegistrationMessage(UUID.randomUUID(), ProductType.PACKAGE,
        "PKG-1", LocalDate.now(), List.of(tray1));
    PendingProduct pending = new PendingProduct(message.productId(), sender.toJson(message), "down");
    when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(pending));
    rabbitIsDown();

    new PendingProductRetryJob(repository, sender).resendPendingProducts();

    verify(repository, never()).delete(any(PendingProduct.class));
    verify(repository).save(pending);
    assertEquals(2, pending.getAttempts());
  }
}
