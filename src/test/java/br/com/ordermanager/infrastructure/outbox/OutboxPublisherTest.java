package br.com.ordermanager.infrastructure.outbox;

import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.entities.enums.OrderStatus;
import br.com.ordermanager.domain.repositories.OrderRepository;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxPublisherTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Captor
    private ArgumentCaptor<Outbox> outboxCaptor;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void shouldPublishEventsAndMarkAsProcessed() {
        // Arrange
        var outbox1 = new Outbox();
        outbox1.setId(UUID.randomUUID());
        outbox1.setAggregateId(UUID.randomUUID());
        outbox1.setPayload("{\"externalOrderId\":\"a1b2c3d4-e5f6-7890-1234-567890abcdef\"}");
        outbox1.setProcessed(false);

        var outbox2 = new Outbox();
        outbox2.setId(UUID.randomUUID());
        outbox2.setAggregateId(UUID.randomUUID());
        outbox2.setPayload("{\"externalOrderId\":\"f0e9d8c7-b6a5-4321-0987-6543210fedcba\"}");
        outbox2.setProcessed(false);

        var outboxList = Arrays.asList(outbox1, outbox2);
        when(outboxRepository.findByProcessedFalse()).thenReturn(outboxList);

        var order1 = new Order();
        order1.setExternalOrderId(outbox1.getAggregateId());
        when(orderRepository.findByExternalOrderId(outbox1.getAggregateId())).thenReturn(order1);

        var order2 = new Order();
        order2.setExternalOrderId(outbox2.getAggregateId());
        when(orderRepository.findByExternalOrderId(outbox2.getAggregateId())).thenReturn(order2);

        // Act
        outboxPublisher.publishOutBoxEvents();

        // Assert
        verify(kafkaTemplate, times(2)).send(any(), anyString());
        verify(outboxRepository, times(2)).save(outboxCaptor.capture());
        var savedOutboxes = outboxCaptor.getAllValues();
        assertTrue(savedOutboxes.stream().allMatch(Outbox::isProcessed));

        verify(orderRepository, times(2)).save(orderCaptor.capture());
        var savedOrders = orderCaptor.getAllValues();
        assertTrue(savedOrders.stream().allMatch(order -> order.getStatus() == OrderStatus.SENT));
    }

    @Test
    void shouldNotPublishEventsWhenListIsEmpty() {
        // Arrange
        when(outboxRepository.findByProcessedFalse()).thenReturn(Collections.emptyList());

        // Act
        outboxPublisher.publishOutBoxEvents();

        // Assert
        verify(kafkaTemplate, never()).send(any(), anyString());
        verify(outboxRepository, never()).save(any(Outbox.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldHandleExceptions() {
        // Arrange
        var outbox = new Outbox();
        outbox.setId(UUID.randomUUID());
        outbox.setAggregateId(UUID.randomUUID());
        outbox.setPayload("{\"externalOrderId\":\"a1b2c3d4-e5f6-7890-1234-567890abcdef\"}");
        outbox.setProcessed(false);

        when(outboxRepository.findByProcessedFalse()).thenReturn(Collections.singletonList(outbox));
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        // Act
        outboxPublisher.publishOutBoxEvents();

        // Assert
        verify(kafkaTemplate, times(1)).send(any(), anyString());
        verify(outboxRepository, never()).save(any(Outbox.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}